package scala_lang

object JoinGroup extends App {
  val r = (1 to 100).filter(_%2!=0).fold(0){(a,b)⇒a+b*b}
  println(r)

}
