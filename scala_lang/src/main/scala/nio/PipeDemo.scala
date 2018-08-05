package nio

import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.Pipe

object PipeDemo extends App {
  val BufSize = 10
  val Limit   = 3

  val pipe       = Pipe.open()
  val senderTask = new Runnable {
    override def run(): Unit = {
      val src = pipe.sink()
      val buffer = ByteBuffer.allocate(BufSize)
      (0 to Limit).foreach { i ⇒
        buffer.clear()
        (0 to BufSize).foreach { _ ⇒
          buffer.put((Math.random() * 256).toByte)
        }

        buffer.flip()

        try {
          while (src.write(buffer) > 0) {}
        } catch {
          case ioe: IOException ⇒
            println(ioe.getMessage)
        }
      }

      src.close()
    }
  }

  val receiverTask = new Runnable {
    override def run(): Unit = {
      val dst = pipe.source()
      val buffer = ByteBuffer.allocate(BufSize)
      try {
        while (dst.read(buffer) >= 0) {
          buffer.flip()
          while (buffer.remaining() > 0)
            println(buffer.get & 255)
          buffer.clear()
        }
      } catch {
        case ioe: IOException ⇒
          println(ioe.getMessage)
      }
    }
  }

  val sender   = new Thread(senderTask)
  val receiver = new Thread(receiverTask)
  sender.start()
  receiver.start()
}
