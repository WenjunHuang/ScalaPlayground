package cat

import cats.Monad
import cats.instances.option._
import cats.instances.list._
import cats.instances.future._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object CatsMonad extends App {

  val opt1 = Monad[Option].pure(3)
  val opt2 = Monad[Option].flatMap(opt1)(a ⇒ Some(a + 2))
  val opt3 = Monad[Option].map(opt2)(a ⇒ 100 * a)

  val list1 = Monad[List].pure(3)
  val list2 = Monad[List].flatMap(List(1, 2, 3))(x ⇒ List(x, x * 10))
  val list3 = Monad[List].map(list2)(_ + 123)

  val fm     = Monad[Future]
  val future = fm.flatMap(fm.pure(1))(x ⇒ fm.pure(x + 2))
  Await.result(future, 1.second)

}
