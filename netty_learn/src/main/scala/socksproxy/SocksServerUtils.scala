package socksproxy

import io.netty.buffer.Unpooled
import io.netty.channel.{Channel, ChannelFutureListener}


object SocksServerUtils {
  def closeOnFlush(ch:Channel) = {
    if (ch.isActive)
      ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE)
  }
}
