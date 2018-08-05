package fpinscala.datastructures

sealed trait List[+A]

case object Nil extends List[Nothing]

case class Cons[+A](head: A, tail: List[A]) extends List[A]

object List {
  def sum(ints: List[Int]): Int = ints match {
    case Nil ⇒ 0
    case Cons(x, xs) ⇒ x + sum(xs)
  }

  def product(ds: List[Double]): Double = ds match {
    case Nil ⇒ 1.0
    case Cons(x, xs) ⇒ x * product(xs)
  }

  def foldRight[A, B](as: List[A], z: B)(f: (A, B) ⇒ B): B = as match {
    case Nil ⇒ z
    case Cons(x, xs) ⇒ f(x, foldRight(xs, z)(f))
  }

  def foldRight2[A,B](as:List[A],z:B)(f:(A,B)⇒B):B = {
    foldLeft(reverse(as),z){(x,y)⇒f(y,x)}
  }

  def foldLeft[A,B](as:List[A],z:B)(f:(B,A)⇒B):B = as match {
    case Nil ⇒ z
    case Cons(x,xs)⇒foldLeft(xs,f(z,x))(f)
  }

  def sum2(ints: List[Int]): Int = foldRight(ints, 0)((x, y) ⇒ x + y)

  def sum3(ns:List[Int]):Int = foldLeft(ns,0)(_ + _)

  def product2(ns: List[Double]): Double = foldRight(ns, 1.0)(_ * _)

  def product3(ns:List[Double]):Double = foldLeft(ns,1.0)(_*_)

  def length[A](as:List[A]):Int = foldRight(as,0)((_,y)⇒y + 1)

  def length2[A](as:List[A]):Int = foldLeft(as,0)((x,_)⇒x+1)

  def reverse[A](as:List[A]):List[A] = foldLeft(as,Nil:List[A])((x,y)⇒Cons(y,x))

  def apply[A](as: A*): List[A] = if (as.isEmpty) Nil
    else Cons(as.head, apply(as.tail: _*))

  def drop[A](l: List[A], n: Int): List[A] = l match {
    case Nil ⇒ Nil
    case Cons(_, tail) ⇒ drop(tail, n - 1)
  }

  def tail[A](ds: List[A]): List[A] = drop(ds, 1)

  def init[A](l: List[A]): List[A] = l match {
    case Nil ⇒ Nil
    case Cons(_, Nil) ⇒ Nil
    case Cons(h, tail) ⇒ Cons(h, init(tail))
  }

  def dropWhile[A](l: List[A])(f: A ⇒ Boolean): List[A] = l match {
    case Cons(h, tail) if f(h) ⇒ dropWhile(tail)(f)
    case _ ⇒ l
  }

  def setHead[A](replace: A, ds: List[A]) = ds match {
    case Nil ⇒ Cons(replace, Nil)
    case c@Cons(_, _) ⇒ Cons(replace, c)
  }

  def append[A](a:A,as:List[A]):List[A] = foldRight(as,Cons(a,Nil)){(x,y)⇒Cons(x,y)}

  def append2[A](a:A,as:List[A]):List[A] = foldLeft(reverse(as),Cons(a,Nil)){(x,y)⇒Cons(y,x)}

  def flatten[A](as:List[List[A]]):List[A] = foldLeft(as,Nil:List[A]){(x,y)⇒
    foldLeft(y,x){(x1,y1)⇒append(y1,x1)}
  }

  def map[A,B](as:List[A])(f:A⇒B):List[B] = as match {
    case Nil ⇒ Nil
    case Cons(a,Nil)⇒Cons(f(a),Nil)
    case Cons(a,xs) ⇒ Cons(f(a),map(xs)(f))
  }

  def filter[A](as:List[A])(f:A⇒Boolean):List[A] = as match {
    case Nil ⇒ Nil
    case Cons(x,xs)⇒ if (f(x)) Cons(x,filter(xs)(f)) else filter(xs)(f)
  }

  def flatMap[A,B](as:List[A])(f:A⇒List[B]):List[B] = as match {
    case Nil ⇒ Nil
    case Cons(x,xs) ⇒ foldRight(f(x),flatMap(xs)(f))(Cons(_,_))
  }

  def filter2[A](as:List[A])(f:A⇒Boolean):List[A] = flatMap(as){i⇒
    if (f(i))
      List(i)
    else
      Nil
  }

  def zipWith[A,B,C](one:List[A],two:List[B])(f: (A,B) ⇒ C):List[C] = (one,two) match{
    case (Nil,_) ⇒ Nil
    case (_,Nil) ⇒Nil
    case (Cons(a,ax),Cons(b,bx))⇒Cons(f(a,b),zipWith(ax,bx)(f))
  }

//  def hasSubsequence[A](sup:List[A],sub:List[A]):Boolean = {
//    def compare(sp:List[A],sb:List[A]) =(sp,sb)  match {
//      case (Nil,Cons(_,_)) ⇒ false
//      case (_,Nil)⇒true
//      case (Cons(a,ax),Cons(b,bx))⇒if (a == b) hasSubsequence(ax,bx) else false
//    }
//
//    if (compare(sup,sub))
//      true
//    else{
//      sup match {
//        case Nil⇒false
//        case Con
//      }
//    }
//  }
}

