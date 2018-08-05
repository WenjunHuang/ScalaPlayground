import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

object UUIDTiming extends App {


  def runUUID(count:Int) = {
    (0 to count).foreach { _ ⇒
      UUID.randomUUID().getLeastSignificantBits
    }
  }

  var count = new AtomicLong
  def runCount(c:Int) = {
    (0 to c).foreach{_⇒
      count.incrementAndGet()
    }
  }


  // warmup
  runUUID(20000)
  runCount(20000)

  var start = System.currentTimeMillis()
  runUUID(10000)
  var end = System.currentTimeMillis()
  println(s"uuid: ${end - start}")

  start = System.currentTimeMillis()
  runCount(20000)
  end = System.currentTimeMillis()
  println(s"AtomicLong: ${end - start}")
}
