package unsafe

class A {
  val a = 1
}

object AvoidConstructor extends App {
  val o1 = new A
  println(o1.a)

  val o2 = classOf[A].newInstance()
  println(o2.a)

  val o3 = getUnsafe().allocateInstance(classOf[A]).asInstanceOf[A]
  println(o3.a)
}
