package socksproxy

import io.netty.channel.{ChannelHandler, ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.socks.SocksProtocolVersion
import io.netty.handler.codec.socksx.{SocksMessage, SocksVersion}
import io.netty.handler.codec.socksx.v4.{Socks4CommandRequest, Socks4CommandType}
import io.netty.handler.codec.socksx.v5._

@ChannelHandler.Sharable
class SocksServerHandler extends SimpleChannelInboundHandler[SocksMessage] {
  override def channelRead0(ctx: ChannelHandlerContext, msg: SocksMessage): Unit = {
    msg.version() match {
      case SocksVersion.SOCKS4a ⇒
        val socksV4CmdRequest = msg.asInstanceOf[Socks4CommandRequest]
        if (socksV4CmdRequest.`type`() == Socks4CommandType.CONNECT) {
          ctx.pipeline().addLast(new SocksServerConnectHandler())
          ctx.pipeline().remove(this)
          ctx.fireChannelRead(msg)
        }
      case SocksVersion.SOCKS5 ⇒
        msg match {
          case req: Socks5InitialRequest ⇒
            ctx.pipeline().addFirst(new Socks5CommandRequestDecoder())
            ctx.write(new DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH))
          case req: Socks5PasswordAuthRequest⇒
            ctx.pipeline().addFirst(new Socks5CommandRequestDecoder())
            ctx.write(new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.SUCCESS))
          case req: Socks5CommandRequest ⇒
            if (req.`type`() == Socks5CommandType.CONNECT){
              ctx.pipeline().addLast(new SocksServerConnectHandler())
              ctx.pipeline().remove(this)
              ctx.fireChannelRead(msg)
            }else{
              ctx.close()
            }
          case _=> ctx.close()
        }
      case SocksVersion.UNKNOWN ⇒
        ctx.close()
    }
  }

  override def channelReadComplete(ctx:ChannelHandlerContext) ={
    ctx.flush()
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    SocksServerUtils.closeOnFlush(ctx.channel())
  }
}

object SocksServerHandler {
  lazy val Instance = new SocksServerHandler
}
