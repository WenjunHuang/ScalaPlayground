package fpinscala.datastructures

import scala.annotation.tailrec

object Option {

  def apply[A](value:A):Option[A] = Some(value)

  def variance(xs:Seq[Double]):Option[Double] = {
      val m = mean(xs)
    m.flatMap{mean ⇒
      Some(xs.foldLeft(0.0){(accum,x)⇒
        accum + math.pow(x - mean,2)
      })
    }
  }

  def mean(xs:Seq[Double]):Option[Double] = {
    if (xs.isEmpty) None
    else Some(xs.foldLeft(0.0)(_+_) / xs.size)
  }

  def lift[A,B](f:A⇒B):Option[A] ⇒ Option[B] = _ map f

  def map2[A,B,C](a:Option[A],b:Option[B])(f:(A,B)⇒C):Option[C] =
    a flatMap (aa ⇒
    b map (bb⇒ f(aa,bb)))

  def map3[A,B,C](a:Option[A],b:Option[B])(f:(A,B)⇒C):Option[C] =
    for {
      aa ← a
      bb ← b
    } yield f(aa,bb)

  def sequence[A](a:List[Option[A]]):Option[List[A]] = {
   traverse(a)(identity)
  }

  def Try[A](value: ⇒A):Option[A] = try Some(value)
    catch {case _: Exception ⇒ None }

  def parseInts(a:List[String]):Option[List[Int]] =
    sequence(List.map(a)(i⇒Try(i.toInt)))

  def traverse[A,B](a:List[A])(f:A⇒Option[B]):Option[List[B]]= a match{
    case Nil ⇒ Some(Nil)
    case Cons(x,xs)⇒
      val optX = f(x)
      optX match {
        case None ⇒ None
        case Some(value)⇒
          val optT = traverse(xs)(f)
          optT match {
            case None⇒ None
            case Some(list)⇒Some(Cons(value,list))
          }
    }
  }
}

sealed trait Option[+A] {
  def map[B](f: A ⇒ B): Option[B] = this match {
    case None ⇒ None
    case Some(value) ⇒ Some(f(value))
  }

  def flatMap[B](f: A ⇒ Option[B]): Option[B] = this match {
    case None ⇒ None
    case Some(value) ⇒ f(value)
  }

  def getOrElse[B >: A](default: ⇒ B): B = this match {
    case None ⇒ default
    case Some(value) ⇒ value
  }

  def orElse[B >: A](ob: => Option[B]): Option[B] = this match {
    case Some(_) ⇒ this
    case None ⇒ ob
  }

  def filter(f:A ⇒ Boolean):Option[A] = this match {
    case None ⇒ None
    case Some(value) ⇒ if(f(value)) this else None
  }

}

case class Some[+A](get: A) extends Option[A]

case object None extends Option[Nothing]

