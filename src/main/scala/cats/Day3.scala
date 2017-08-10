package cats

import simulacrum._

@typeclass trait CanTruthy[A] {
  self =>
  def truthy(a: A): Boolean
}

object CanTruthy {
  def fromTruthy[A](f: A => Boolean): CanTruthy[A] =
    (a: A) => f(a)
}

@typeclass trait CanAppend[A] {
  @op("|+|") def append(a1: A, a2: A): A
}


