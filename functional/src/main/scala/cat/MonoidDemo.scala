package cat

class MonoidDemo {}

trait Semigroup[A] {
  def combine(x:A, y:A):A
}

trait Monoid[A] extends Semigroup[A] {
  def empty:A
}

trait Functor[F[_]]{
  def map[A,B](fa:F[A])(f:A⇒B):F[B]
}

trait Monad[F[_]] {
  def pure[A](a:A):F[A]
  def flatMap[A,B](value:F[A])(func:A⇒F[B]):F[B]
  def map[A,B](value:F[A])(func:A⇒B):F[B] = {
    flatMap(value)(a ⇒ pure(func(a)))
  }
}