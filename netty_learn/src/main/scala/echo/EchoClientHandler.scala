package echo

import java.net.InetSocketAddress

import io.netty.bootstrap.Bootstrap
import io.netty.buffer.{ByteBuf, Unpooled}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.channel.{ChannelHandlerContext, ChannelInitializer, SimpleChannelInboundHandler}
import io.netty.util.CharsetUtil

import scala.util.Try

object EchoClientHandler extends SimpleChannelInboundHandler[ByteBuf] {
  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    ctx.writeAndFlush(Unpooled.copiedBuffer("Netty  rocks", CharsetUtil.UTF_8))
  }

  override def channelRead0(ctx: ChannelHandlerContext, msg: ByteBuf): Unit = {
    println(s"Client received: ${msg.toString(CharsetUtil.UTF_8)}")
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }

  def start = {
    val group = new NioEventLoopGroup()
    try {
      val b = new Bootstrap()
      b.group(group)
        .channel(classOf[NioSocketChannel])
        .remoteAddress(new InetSocketAddress("localhost",8080))
        .handler(new ChannelInitializer[SocketChannel] {
          override def initChannel(ch: SocketChannel): Unit =  {
            ch.pipeline().addLast( EchoClientHandler )
          } })
      val f = b.connect().sync()
    } finally {
      group.shutdownGracefully().sync
    }
  }

  def main(args: Array[String]): Unit = {
    start
  }
}
