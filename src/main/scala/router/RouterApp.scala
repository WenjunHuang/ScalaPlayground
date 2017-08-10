package router

import akka.actor.{ActorSystem, Props}
import akka.pattern._
import akka.util.Timeout

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object RouterApp extends App {
  implicit val system = ActorSystem("RouterApp")
  implicit val dispatcher = system.dispatcher
  implicit val timeout = Timeout(5.seconds)

  val router = system.actorOf(Props(new MasterActor(Props(classOf[FooActor]))))

  val f = for (i <- 1 to 1000) yield router ? "ping"
  val future = Future.traverse(f) { it => it }

  println(Await.result(future, Duration.Inf))
  system.terminate()

}
