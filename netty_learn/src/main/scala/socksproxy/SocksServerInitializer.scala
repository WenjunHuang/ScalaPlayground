package socksproxy

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.socksx.SocksPortUnificationServerHandler
import io.netty.handler.logging.{LogLevel, LoggingHandler}

class SocksServerInitializer extends ChannelInitializer[SocketChannel] {
  override def initChannel(ch: SocketChannel): Unit = {
    ch.pipeline().addLast(
      //      new LoggingHandler(LogLevel.INFO),
      new SocksPortUnificationServerHandler())
    ch.pipeline().addLast(SocksServerHandler.Instance)
  }
}
