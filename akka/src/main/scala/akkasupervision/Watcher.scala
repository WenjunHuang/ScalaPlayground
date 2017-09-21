package akkasupervision

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, PoisonPill, Props, Terminated}
import akka.pattern._
import akka.util.Timeout
import akkasupervision.ActorLifeCycle.actorSystem

import scala.concurrent.duration._
import scala.concurrent.Await

class Watcher(beingWatch: ActorRef) extends Actor with ActorLogging {
  context.watch(beingWatch)

  override def receive = {
    case Terminated(actorRef) ⇒
      log.warning("Actor {} terminated", actorRef)
  }
}

class BeingWatch extends Actor{
  override def receive = {
    case "die"⇒
      throw new Exception
  }
}

object Main extends App {
  implicit val timout = Timeout(2 seconds)
  val actorSystem = ActorSystem("ClusterSystem")

  val supervisor = actorSystem.actorOf(Props[Supervisor],"supervisor")
  val childFuture = supervisor ? (Props(new LifeCycleActor),"LiftCycleActor")
  val child = Await.result(childFuture.mapTo[ActorRef],2 seconds)

  val watch = actorSystem.actorOf(Props(classOf[Watcher], child))

  child ! StopError
}

