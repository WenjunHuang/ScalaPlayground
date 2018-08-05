package nio

import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util.Date

object SelectorClient extends App{
  val port = 9999
  val bb = ByteBuffer.allocateDirect(8)

  val sc = SocketChannel.open()
  val addr = new InetSocketAddress("localhost",port)
  sc.connect(addr)

  var time = 0l
  while (sc.read(bb) != -1){
    bb.flip()
    while(bb.hasRemaining) {
      time <<= 8
      time |= bb.get() & 255
    }
    bb.clear()
  }
  println(new Date(time))
  sc.close()

}
