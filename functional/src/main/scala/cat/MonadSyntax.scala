package cat

import cats.Monad
import cats.syntax.functor._
import cats.syntax.flatMap._
import cats.instances.option._
import cats.instances.list._

import scala.language.higherKinds

object MonadSyntax extends App {
  def sumSquare[F[_] : Monad](a: F[Int], b: F[Int]): F[Int] = {
    for {
      x←a
      y←b
    } yield x*x + y*y
  }

  val v = sumSquare(Option(3),Option(4))
}
