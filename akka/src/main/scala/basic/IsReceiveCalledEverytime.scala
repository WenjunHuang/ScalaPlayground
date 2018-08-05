package basic

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

class FooActor extends Actor with ActorLogging {
  override def receive = {
    log.info("received call")
    val r:Receive = {
      case "hello" ⇒ "world"
    }
    r
  }
}

object IsReceiveCalledEverytime extends App {

  val system = ActorSystem("test")
  val foo    = system.actorOf(Props[FooActor])

  (0 to 10).foreach { i ⇒ foo ! "hello" }

  sys.addShutdownHook(system.terminate())

}
