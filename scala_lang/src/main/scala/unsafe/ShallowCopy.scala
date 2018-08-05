package unsafe

class B {
  val b = 0
  val y = 0L
}

object ShallowCopy extends App {
  def normalize(value: Int): Long = {
    if (value >= 0) value
    else {
      (~0L >>> 32) & value
    }
  }

  def sizeOf(obj: Any): Long = {
    getUnsafe().getAddress(normalize(getUnsafe().getInt(obj, 4L)) + 12L)
  }

//  def shallowCopy[T](obj: T): T = {
//    val size =
//  }

  val b = new B
  println(sizeOf(b))

}
