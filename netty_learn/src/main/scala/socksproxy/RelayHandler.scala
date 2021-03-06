package socksproxy

import io.netty.buffer.Unpooled
import io.netty.channel.{Channel, ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.util.ReferenceCountUtil

class RelayHandler(relayChannel: Channel) extends ChannelInboundHandlerAdapter {
  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
    if (relayChannel.isActive) {
      relayChannel.writeAndFlush(msg)
    } else {
      ReferenceCountUtil.release(msg)
    }
  }

  override def channelInactive(ctx: ChannelHandlerContext): Unit = {
    if (relayChannel.isActive()) {
      SocksServerUtils.closeOnFlush(relayChannel)
    }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    cause.printStackTrace()
    ctx.close()
  }
}
