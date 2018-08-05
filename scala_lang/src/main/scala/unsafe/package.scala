import sun.misc.Unsafe

package object unsafe {
  def getUnsafe(): Unsafe = {
    val f = classOf[Unsafe].getDeclaredField("theUnsafe")
    f.setAccessible(true)
    f.get(null).asInstanceOf[Unsafe]
  }

}
