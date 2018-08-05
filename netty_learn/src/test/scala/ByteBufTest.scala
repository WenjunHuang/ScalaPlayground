import io.netty.buffer.{ByteBuf, PooledByteBufAllocator}
import io.netty.util.IllegalReferenceCountException
import org.scalatest.{FunSuite, Matchers, WordSpec}

class ByteBufTest extends FunSuite with Matchers {
  test("ByteBuf is reference counted") {
    val pool = new PooledByteBufAllocator()
    val buf = pool.directBuffer()

    buf.refCnt() should be(1)

    buf.release() should be(true)
    buf.refCnt() should be(0)
    an[IllegalReferenceCountException] should be thrownBy buf.writeLong(10L)
  }

  test("release") {
    val pool = new PooledByteBufAllocator()
    val buf = pool.directBuffer()
    c(b(a(buf)))

    buf.refCnt() should be(0)
  }

  def a(input: ByteBuf): ByteBuf = {
    input.writeByte(42)
    input
  }

  def b(input: ByteBuf): ByteBuf = {
    try {
      val output = input.alloc().directBuffer(input.readableBytes() + 1)
      output.writeBytes(input)
      output.writeByte(42)
      output
    } finally {
      input.release()
    }
  }

  def c(input: ByteBuf) = {
    println(input)
    input.release()
  }

}
