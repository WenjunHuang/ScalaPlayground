package basic

import akka.actor.{Actor, ActorSystem, Props, Status}
import akka.util.Timeout
import akka.pattern._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class ReturnExceptionActor extends Actor {
  override def receive: Receive = {
    // 必须使用Status.Failure才会触发ask的Future fail
    case _ ⇒ sender() ! Status.Failure(new Exception("foo"))
  }
}

object AskReturnException extends App {
  implicit val system  = ActorSystem("foo")
  implicit val timeout = Timeout(5 seconds)
  implicit val ec = system.dispatcher

  val fooRef = system.actorOf(Props[ReturnExceptionActor])
  val fut = fooRef ? "something"

  fut.onComplete {
    case Success(result) ⇒ println(s"error $result")
    case Failure(cause) ⇒ println(cause)
  }

//  Await.(fut, 10 seconds)
//  system.terminate()
}
