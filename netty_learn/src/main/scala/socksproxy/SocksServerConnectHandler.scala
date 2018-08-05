package socksproxy

import io.netty.bootstrap.Bootstrap
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel._
import io.netty.handler.codec.socksx.SocksMessage
import io.netty.handler.codec.socksx.v4.{DefaultSocks4CommandResponse, Socks4CommandRequest, Socks4CommandStatus}
import io.netty.handler.codec.socksx.v5.{DefaultSocks5CommandResponse, Socks5CommandRequest, Socks5CommandStatus}
import io.netty.util.concurrent.Future

@ChannelHandler.Sharable
class SocksServerConnectHandler extends SimpleChannelInboundHandler[SocksMessage] {
  val bootstrap = new Bootstrap()

  override def channelRead0(ctx: ChannelHandlerContext, msg: SocksMessage): Unit = {
    msg match {
      case req: Socks4CommandRequest ⇒
        val promise = ctx.executor().newPromise[Channel]()
        promise.addListener { future: Future[Channel] ⇒
          val outboundChannel = future.getNow
          if (future.isSuccess) {
            val responseFuture = ctx.channel().writeAndFlush(
              new DefaultSocks4CommandResponse(Socks4CommandStatus.SUCCESS)
            )
            responseFuture.addListener { future: ChannelFuture ⇒
              ctx.pipeline().remove(SocksServerConnectHandler.this)
              outboundChannel.pipeline().addLast(new RelayHandler(ctx.channel()))
              ctx.pipeline().addLast(new RelayHandler(outboundChannel))
            }
          } else {
            ctx.channel().writeAndFlush(
              new DefaultSocks4CommandResponse(Socks4CommandStatus.REJECTED_OR_FAILED))
            SocksServerUtils.closeOnFlush(ctx.channel())
          }
        }

        val inboundChannel = ctx.channel()
        bootstrap.group(inboundChannel.eventLoop())
          .channel(classOf[NioSocketChannel])
          .option[Integer](ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
          .option[java.lang.Boolean](ChannelOption.SO_KEEPALIVE, true)
          .handler(new DirectClientHandler(promise))

        bootstrap.connect(req.dstAddr(), req.dstPort()).addListener { future: ChannelFuture ⇒
          if (future.isSuccess) {
            // Connection established use handler provided results
          } else {
            ctx.channel().writeAndFlush(
              new DefaultSocks4CommandResponse(Socks4CommandStatus.REJECTED_OR_FAILED)
            )
            SocksServerUtils.closeOnFlush(ctx.channel())
          }
        }
      case req: Socks5CommandRequest ⇒
        val promise = ctx.executor().newPromise[Channel]()

        promise.addListener { future: Future[Channel] ⇒
          val outboundChannel = future.getNow
          if (future.isSuccess) {
            val responseFuture = ctx.channel().writeAndFlush(new DefaultSocks5CommandResponse(
              Socks5CommandStatus.SUCCESS,
              req.dstAddrType(),
              req.dstAddr(),
              req.dstPort()
            ))
            responseFuture.addListener { channelFuture: ChannelFuture ⇒
              ctx.pipeline().remove(SocksServerConnectHandler.this)
              outboundChannel.pipeline().addLast(new RelayHandler(ctx.channel()))
              ctx.pipeline().addLast(new RelayHandler(outboundChannel))
            }
          } else {
            ctx.channel()
              .writeAndFlush(new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, req.dstAddrType()))
            SocksServerUtils.closeOnFlush(ctx.channel())
          }
        }
        val inboundChannel = ctx.channel()
        bootstrap.group(inboundChannel.eventLoop())
          .channel(classOf[NioSocketChannel])
          .option[Integer](ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
          .option[java.lang.Boolean](ChannelOption.SO_KEEPALIVE, true)
          .handler(new DirectClientHandler(promise))
        bootstrap.connect(req.dstAddr(), req.dstPort()).addListener(new ChannelFutureListener {
          override def operationComplete(future: ChannelFuture): Unit = {
            if (future.isSuccess) {
              // Connection established use handler provided results
            }
            else {
              ctx.channel().writeAndFlush(
                new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, req.dstAddrType())
              )
              SocksServerUtils.closeOnFlush(ctx.channel())
            }
          }
        })
      case _ ⇒
        ctx.close()
    }
  }

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    ctx.flush()
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    SocksServerUtils.closeOnFlush(ctx.channel())
  }
}
