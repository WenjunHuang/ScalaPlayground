package echo

import java.net.InetSocketAddress

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.{NioEventLoop, NioEventLoopGroup}
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel

object EchoServer extends App {
  if (args.length != 1) {
    println(s"Usage: ${EchoServer.getClass.getSimpleName}")
    sys.exit(-1)
  }

  val port = args(0).toInt
  start

  def start = {
    val serverHandler = new EchoServerHandler
    val group = new NioEventLoopGroup()
    try {
      val b = new ServerBootstrap()
      b.group(group)
        .channel(classOf[NioServerSocketChannel])
        .localAddress(new InetSocketAddress(port))
        .childHandler(new ChannelInitializer[SocketChannel] {
          override def initChannel(ch: SocketChannel): Unit = {
            ch.pipeline().addLast(serverHandler)
          }
        })

      val f = b.bind().sync()
      f.channel().closeFuture().sync()
    }finally {
      group.shutdownGracefully().sync()
    }
  }

}
