package nio

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.{SelectionKey, Selector, ServerSocketChannel}
import scala.collection.convert.ImplicitConversions._

object SelectorServer extends App{
  val DefaultPort = 9999
  val bb = ByteBuffer.allocateDirect(8)

  println(s"Server starting ... listening on port $DefaultPort")

  val ssc = ServerSocketChannel.open()
  val ss = ssc.socket()
  ss.bind(new InetSocketAddress(DefaultPort))
  ssc.configureBlocking(false)

  val s = Selector.open()
  ssc.register(s, SelectionKey.OP_ACCEPT)

  while (true) {
    val n = s.select()
    if (n != 0){
      val keys = s.selectedKeys()
      val it = keys.iterator()
      it.foreach{key⇒
        if (key.isAcceptable) {
          val sc = key.channel().asInstanceOf[ServerSocketChannel].accept()
          if (sc != null) {
            println("Receiving connection")
            bb.clear()
            bb.putLong(System.currentTimeMillis())
            bb.flip()
            println("Writing current time")
            while (bb.hasRemaining)
              sc.write(bb)
            sc.close()
          }
        }
      }
      keys.clear()
    }

  }

}
