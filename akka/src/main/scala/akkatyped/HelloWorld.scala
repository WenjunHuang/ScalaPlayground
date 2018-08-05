package akkatyped

import akka.typed._
import akka.typed.scaladsl.Actor
import akka.typed.scaladsl.AskPattern._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import HelloWorld._
import akka.util.Timeout

object HelloWorldApp extends App {
  implicit val timeout                    = Timeout(3 seconds)
  implicit val system: ActorSystem[Greet] = ActorSystem(greeter, "hello")
  implicit val scheduler = system.scheduler
  val future: Future[Greeted] = system ? (Greet("world", _))

  for {
    greeting ← future.recover { case ex ⇒ ex.getMessage }
    done ← {
      println(s"result: $greeting"); system.terminate()
    }
  } println("system terminated")
}

object HelloWorld {

  final case class Greet(whom: String, replyTo: ActorRef[Greeted])

  final case class Greeted(whom: String)

  val greeter = Actor.immutable[Greet] { (_, msg) ⇒
    println(s"Hello ${msg.whom}!")
    msg.replyTo ! Greeted(msg.whom)
    Actor.same
  }
}
