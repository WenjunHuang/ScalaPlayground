package socksproxy

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.{NioEventLoop, NioEventLoopGroup}
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.{LogLevel, LoggingHandler}
import io.netty.util.internal.logging.{InternalLoggerFactory, Slf4JLoggerFactory}

object SocksServer extends App {
  InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE)
  val bossGroup   = new NioEventLoopGroup(1)
  val workerGroup = new NioEventLoopGroup()

  val bootstrap = new ServerBootstrap()
  bootstrap.group(bossGroup, workerGroup)
    .channel(classOf[NioServerSocketChannel])
    .handler(new LoggingHandler(LogLevel.INFO))
    .childHandler(new SocksServerInitializer())
  bootstrap.bind(1080).sync().channel().closeFuture().sync()

  bossGroup.shutdownGracefully()
  workerGroup.shutdownGracefully()

}
