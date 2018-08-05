package scala_lang

import scala.reflect.ClassTag

object CT extends App {

  def method[T](v:MyClass[T]) = {
    v match {
      case MyClass(v,t) if t.runtimeClass == classOf[List[Double]] ⇒
        println("ok")
      case MyClass(_,t) ⇒
        println(t)
        println("not ok")
    }
  }

  method(MyClass(List(10),implicitly[ClassTag[List[Int]]]))
  method(MyClass("string",implicitly[ClassTag[String]]))

}

case class MyClass[T](val value: T, tag: ClassTag[T])

object MyClass {
//  def apply[T](value: T)(implicit tag: ClassTag[T]) = {
//    new MyClass(value, implicitly[ClassTag[T]])
//  }
}
