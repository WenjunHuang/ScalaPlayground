import java.net.InetSocketAddress

import io.netty.bootstrap.{Bootstrap, ServerBootstrap}
import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelFuture, ChannelFutureListener, ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.{NioServerSocketChannel, NioSocketChannel}

object ProxyApp extends App {
  val bootstrap = new ServerBootstrap()
  bootstrap.group(new NioEventLoopGroup(1))
    .channel(classOf[NioServerSocketChannel])
    .childHandler(new SimpleChannelInboundHandler[ByteBuf]() {
      var connectFuture:ChannelFuture = _
      override def channelActive(ctx: ChannelHandlerContext): Unit = {
        val bootstrap = new Bootstrap()
        bootstrap.channel(classOf[NioSocketChannel])
          .handler(new SimpleChannelInboundHandler[ByteBuf]() {
            override def channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf): Unit = {
              ctx.writeAndFlush(msg)
            }
          })
        bootstrap.group(ctx.channel().eventLoop())
        connectFuture = bootstrap.connect(new InetSocketAddress("news.sina.com.cn",80))

      }
      override def channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf): Unit = {

      }
    })

  val future = bootstrap.bind(new InetSocketAddress(8080))
  future.addListener((future: ChannelFuture) => {
    if (future.isSuccess)
      println("Server bound")
    else {
      future.cause().printStackTrace()
    }
  })

}
