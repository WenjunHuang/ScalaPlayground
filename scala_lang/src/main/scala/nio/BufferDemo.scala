package nio

import java.io.{FileInputStream, FileOutputStream, RandomAccessFile}
import java.nio.channels._
import java.nio.{Buffer, ByteBuffer, CharBuffer}

import scala.util.Try

object BufferDemo extends App {
  require(args.length == 1, "usage: program filespec")

  val raf  = new RandomAccessFile(args(0), "rw")
  val fc   = raf.getChannel
  val size = fc.size()
  println(s"Size: $size")

  val mbb = fc.map(FileChannel.MapMode.READ_WRITE, 0, size)

  while (mbb.remaining() > 0)
    println(mbb.get.toChar)

  println()
  println()
  (0 to mbb.limit()).foreach{i ⇒
    val b1 = mbb.get(i)
    val b2 = mbb.get(mbb.limit() - i - 1)
    mbb.put(i,b2)
    mbb.put(mbb.limit() - i - 1,b1)
  }
  mbb.flip()
  while (mbb.remaining() > 0)
    print(mbb.get.toChar)
  fc.close()
}
