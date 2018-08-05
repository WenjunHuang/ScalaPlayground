package fpinscala.`lazy`
import fpinscala.{datastructures ⇒ ds}

sealed trait Stream[+A] {
  def toList:ds.List[A] = this match {
    case Empty ⇒ ds.Nil
    case Cons(v,l)⇒ds.Cons(v(),l().toList)
  }

  def take(n:Int):Stream[A] =
    if (n <= 0) Empty
    else
      this match{
        case Empty⇒Empty
        case Cons(v,l)⇒Cons(v,()⇒l().take(n-1))
      }

  def drop(n:Int):Stream[A] =
    if (n <= 0) Empty
    else this match{
      case Empty ⇒ Empty
      case Cons(_,l)⇒l().drop(n - 1)
    }
}

case object Empty extends Stream[Nothing]

case class Cons[+A](h: () ⇒ A, t: () ⇒ Stream[A]) extends Stream[A]

object Stream {
  def cons[A](hd: ⇒ A, tl: ⇒ Stream[A]): Stream[A] = {
    lazy val head = hd
    lazy val tail = tl
    Cons(() ⇒ head, () ⇒ tail)
  }

  def empty[A]:Stream[A] = Empty

  def apply[A](as:A*):Stream[A] =
    if (as.isEmpty) empty else cons(as.head,apply(as.tail: _*))
}
