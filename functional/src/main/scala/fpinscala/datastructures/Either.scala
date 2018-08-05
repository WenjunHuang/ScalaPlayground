package fpinscala.datastructures

import fpinscala.datastructures.Option.traverse

trait Either[+E, +A] {
  def map[B](f: A⇒B):Either[E,B] = this match {
    case e@Left(_) ⇒ e
    case Right(a)⇒Right(f(a))
  }

  def flatMap[EE >: E,B](f: A ⇒ Either[EE,B]):Either[EE,B] = this match{
    case e@Left(_) ⇒ e
    case Right(a) ⇒ f(a)
  }

  def orElse[EE >: E,B >: A](b: ⇒ Either[EE,B]):Either[EE,B] = this match {
    case Left(_) ⇒ b
    case r@Right(_) ⇒ r
  }

  def map2[EE >: E,B,C](b:Either[EE,B])(f: (A,B) ⇒C):Either[EE,C] = (this,b) match {
    case (a@Left(_),_) ⇒ a
    case (_,b@Left(_)) ⇒ b
    case (Right(a),Right(b)) ⇒ Right(f(a,b))
  }
}

case class Left[+E](value: E) extends Either[E, Nothing]

case class Right[+A](value: A) extends Either[Nothing, A]

object Either {
  def mean(xs: IndexedSeq[Double]): Either[String, Double] =
    if (xs.isEmpty)
      Left("mean of empty list!")
    else
      Right(xs.sum / xs.length)

  def safeDiv(x: Int, y: Int): Either[Exception, Int] =
    try Right(x / y)
    catch {
      case e: Exception ⇒ Left(e)
    }

  def Try[A](a: => A): Either[Exception, A] =
    try Right(a)
    catch {
      case e: Exception ⇒ Left(e)
    }

  def traverse[E,A,B](as:List[A])(f: A⇒Either[E,B]):Either[E,List[B]] = as match {
    case Nil ⇒ Right(Nil)
    case Cons(x,xs)⇒
      val eitherX = f(x)
      eitherX match {
        case l@Left(_) ⇒ l
        case Right(value)⇒
          val optT = traverse(xs)(f)
          optT match {
            case l@Left(_) ⇒ l
            case Right(list)⇒Right(Cons(value,list))
          }
      }
  }

  def sequence[E,A](es:List[Either[E,A]]):Either[E,List[A]] = {
    traverse(es)(identity)
  }
}