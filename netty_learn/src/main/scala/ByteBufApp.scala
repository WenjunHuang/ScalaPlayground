import java.nio.charset.Charset

import io.netty.buffer.{PooledByteBufAllocator, Unpooled}

object ByteBufApp extends App {
  val allocator = new PooledByteBufAllocator
  val buf = allocator.directBuffer()
  println(buf.isDirect)
  assert(buf.refCnt() == 1)


  val destroyed = buf.release()
  assert(destroyed)
  assert(buf.refCnt() == 0)

}
