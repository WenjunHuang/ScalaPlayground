package http.hello

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.logging.{LogLevel, LoggingHandler}
import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.SelfSignedCertificate

object HttpHelloWorldServer extends App {
  val SSL  = System.getProperty("ssl") != null
  val PORT = Integer.parseInt(System.getProperty("port", if (SSL) "8443" else "8080"))

  val sslCtx = if (SSL) {
    val ssc = new SelfSignedCertificate()
    Some(SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build)
  } else {
    None
  }

  val bossGroup = new NioEventLoopGroup(1)
  val workerGroup = new NioEventLoopGroup()

  val bootstrap = new ServerBootstrap()
  bootstrap.option[java.lang.Integer](ChannelOption.SO_BACKLOG,1024)
  bootstrap.group(bossGroup,workerGroup)
    .channel(classOf[NioServerSocketChannel])
    .handler(new LoggingHandler(LogLevel.INFO))
    .childHandler(new HttpHelloWorldServerInitializer(sslCtx))

  val ch = bootstrap.bind(PORT).sync().channel()
  ch.closeFuture().sync()

  bossGroup.shutdownGracefully()
  workerGroup.shutdownGracefully()

}
