package cats
import simulacrum._

object Day2 {

  @typeclass trait Monoid[A] {
    def mzero: A

    @op("|+|") def mappend(a1: A, a2: A): A
  }

  object Monoid {
    val syntax = ops
    implicit val IntMonoid: Monoid[Int] = new Monoid[Int] {
      def mzero: Int = 0

      def mappend(a1: Int, a2: Int): Int = a1 + a2
    }

    implicit val StringMonoid: Monoid[String] = new Monoid[String] {
      override def mzero = ""

      override def mappend(a1: String, a2: String) = a1 + a2
    }

    implicit def OptionMonoid[T: Monoid]: Monoid[Option[T]] = new Monoid[Option[T]] {
      override def mzero = None

      override def mappend(a1: Option[T], a2: Option[T]) = {
        if (a1.isEmpty)
          a2
        else if (a2.isEmpty)
          a1
        else {
          val m = implicitly[Monoid[T]]
          Some(m.mappend(a1.get, a2.get))
        }
      }
    }

    implicit def someToOption[T](value:Some[T]):Option[T] = value
  }

  def sum[M[_]:FoldLeft,A: Monoid](xs: M[A]):A = {
    val m = implicitly[Monoid[A]]
    val fl = implicitly[FoldLeft[M]]
    fl.foldLeft(xs,m.mzero,m.mappend)
  }
}

trait FoldLeft[F[_]] {
  def foldLeft[A,B](xs:F[A],b:B,f:(B,A)⇒B):B
}

object FoldLeft {
  implicit val FoldLeftList:FoldLeft[List] = new FoldLeft[List] {
    def foldLeft[A, B](xs: List[A], b: B, f: (B, A) ⇒ B) = xs.foldLeft(b)(f)
  }
}

