package stopactor

import akka.actor.{Actor, ActorSystem, Props}

object UseStop extends App {
  val actorSystem = ActorSystem("UserStop")

  val ref = actorSystem.actorOf(Props[LongActor])

  for (i ← 1 to 10)
    ref ! "task" + i

  Thread.sleep(1000)
  actorSystem.stop(ref)
}

class LongActor extends Actor {
  override def receive = {
    case msg ⇒
      println(s"processing $msg")
      Thread.sleep(1000) // simulate a long task
  }
}
