package unsafe

class Guard {
  val AccessAllowed = 1

  def giveAccess():Boolean = {
    42 == AccessAllowed
  }
}
object MemoryCorruption extends App {
  var guard = new Guard
  println(guard.giveAccess())

  val f = guard.getClass.getDeclaredField("AccessAllowed")
  val unsafe = getUnsafe()
  unsafe.putInt(guard,unsafe.objectFieldOffset(f),42)

  println(guard.giveAccess())

}
