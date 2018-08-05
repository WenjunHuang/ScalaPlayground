package http.hello

import io.netty.buffer.Unpooled
import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.handler.codec.http._
import io.netty.util.AsciiString
import io.netty.handler.codec.http.HttpResponseStatus._

class HttpHelloWorldServerHandler extends ChannelInboundHandlerAdapter{
  import HttpHelloWorldServerHandler._

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    ctx.flush()
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
    msg match {
      case req:HttpRequest⇒
        val keepAlive = HttpUtil.isKeepAlive(req)
        val response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                                   OK,
                                                   Unpooled.wrappedBuffer(CONTENT))
        response.headers().set(CONTENT_TYPE,"text/plain")
        response.headers().setInt(CONTENT_LENGTH,response.content().readableBytes())

        if (!keepAlive){
          ctx.write(response).addListener(ChannelFutureListener.CLOSE)
        }else {
          response.headers().set(CONNECTION,KEEP_ALIVE)
          ctx.write(response)
        }
      case _⇒
    }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }
}

object HttpHelloWorldServerHandler {
  val CONTENT = Array('H','e','l','l','o',' ','W','o','r','l','d').map(_.toByte)
  val CONTENT_TYPE = AsciiString.cached("Content-Type")
  val CONTENT_LENGTH = AsciiString.cached("Content-Length")
  val CONNECTION = AsciiString.cached("Connection")
  val KEEP_ALIVE = AsciiString.cached("keep-alive")
}
