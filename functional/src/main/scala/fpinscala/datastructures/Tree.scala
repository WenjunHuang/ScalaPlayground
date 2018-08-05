package fpinscala.datastructures

sealed trait Tree[+A]

final case class Leaf[A](value: A) extends Tree[A]

final case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

object Tree {
  def size[A](tree: Tree[A]): Int = tree match {
    case Leaf(_) ⇒ 1
    case Branch(left, right) ⇒ size(left) + size(right) + 1
  }

  def maximum[A: Ordering](tree: Tree[A]): A = tree match {
    case Leaf(value) ⇒ value
    case Branch(left, right) ⇒ implicitly[Ordering[A]].max(maximum(left), maximum(right))
  }

  def depth[A](tree: Tree[A]): Int = tree match {
    case Leaf(_) ⇒ 1
    case Branch(left, right) ⇒ (depth(left) max depth(right)) + 1
  }

  def map[A, B](tree: Tree[A])(f: A ⇒ B): Tree[B] = tree match {
    case Leaf(value) ⇒ Leaf(f(value))
    case Branch(left, right) ⇒ Branch(map(left)(f), map(right)(f))
  }
}
