package macwire
import com.softwaremill.macwire._
import com.github.dwickern.macros.NameOf._

class A() {

}

class C(a:A, specialValue:Int) {
  def foo(): Unit ={
    println(s"$a, C $specialValue")
  }
}

object C {
  def create(a:A) = new C(a,42)
}

trait MyModule {
  lazy val c = wireWith(C.create _)
  val a = wire[A]
}

object FactoryMethods extends App {
  val module = new MyModule {}
  module.c.foo()
}
