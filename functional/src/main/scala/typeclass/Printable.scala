package typeclass

import cats.Show

trait Printable[A] {
  def format(value: A): String
}

object PrintableInstances {
  implicit val stringPrintable = new Printable[String] {
    override def format(value: String) = value
  }

  implicit val intPrintable = new Printable[Int] {
    override def format(value: Int) = value.toString
  }
}

object PrintableSyntax {
  implicit class PrintOps[A](value:A){
    def format(implicit p:Printable[A])=p.format(value)
    def print(implicit p:Printable[A]) = println(p.format(value))
  }
}

object Printable {
  def format[A](value: A)(implicit s: Show[A]): String = s.show(value)

  def print[A](value: A)(implicit s: Show[A]): Unit = println(s.show(value))
}

final case class Cat(name:String,age:Int,color:String)
object Cat {
  implicit val catPrintable = new Printable[Cat]{
    override def format(value: Cat):String =
      s"${value.name} is a ${value.age} year-old ${value.color} cat."
  }

  implicit val catShow = new Show[Cat]{
    override def show(value: Cat) =
      s"${value.name} is a ${value.age} year-old ${value.color} cat."
}
}

object CatApp extends App{
  import PrintableSyntax._
  val cat = Cat("wenjun",36,"yellow")
  cat.print
}