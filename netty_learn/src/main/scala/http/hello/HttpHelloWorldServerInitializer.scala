package http.hello


import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.{HttpServerCodec, HttpServerExpectContinueHandler}
import io.netty.handler.ssl.SslContext

class HttpHelloWorldServerInitializer(sslCtx: Option[SslContext]) extends ChannelInitializer[SocketChannel] {
  override def initChannel(ch: SocketChannel): Unit = {
    val p = ch.pipeline()
    sslCtx match {
      case Some(ctx) ⇒ p.addLast(ctx.newHandler(ch.alloc()))
      case _ ⇒
    }

    p.addLast(new HttpServerCodec())
    p.addLast(new HttpServerExpectContinueHandler)
    p.addLast(new HttpHelloWorldServerHandler)
  }
}
