package scala_lang.nameresolv

import org.scalatest.{FlatSpec, Matchers}

object test {
  val x = 100
}

object otherTest {
  val x = 200
}

trait Foo[T]{}

object Foo {
  implicit val intFoo = new Foo[Int]{
    override def toString = "int foo"
  }
  implicit val stringFoo = new Foo[String] {
    override def toString = "string foo"
  }
}

trait Bar{}
object Bar {
  implicit val barFoo = new Foo[Bar]{
    override def toString = "bar foo"
  }
}

class NameResolvTestSpec extends FlatSpec with Matchers{
  "local name" should "have highest priority" in {
    val x = 1
    import test.x

    x should be(1)
  }

  "explicit import" should "have higher priority than wildcard import " in {
    import test.x
    import otherTest._

    x should be(test.x)
  }

  "" should "not compile" in {
    """
      |val  x = 1;
      |{
      | import test.x
      | x
      |}
    """.stripMargin shouldNot compile
  }

  "implicit search " should "include type class companion object" in {
    def method[T](implicit foo: Foo[T]): String = {
      foo.toString
    }

    method[String] should be("string foo")

    """
      |method[Int]
    """.stripMargin should compile

    """
      |method[Double]
    """.stripMargin shouldNot compile


    """
      |method[Bar]
    """.stripMargin should compile

    method[Bar] should be("bar foo")

  }
}
