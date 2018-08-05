import java.time.ZonedDateTime

import macros._

trait Base {
  val id         : Long
}

@Extends[Base]
case class A()

@Extends[Base]
case class B(xs:List[Long])

object ExtendsTest extends App {
  val anA = A(id=100L)
  println(anA)

}
