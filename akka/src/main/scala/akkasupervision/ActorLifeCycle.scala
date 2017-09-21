package akkasupervision

import akka.actor.SupervisorStrategy.{Escalate, Restart, Stop}
import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props, Stash}
import akka.util.Timeout
import akka.pattern._

import scala.concurrent.Await
import scala.concurrent.duration._

case object Error
case object StopError
case object Increment
case object Print
case class StopActor(actorRef:ActorRef)
case class Resume(sum:Int)

class LifeCycleActor extends Actor with Stash {
  var sum = 1

  override def preRestart(reason: scala.Throwable, message: Option[Any]): Unit = {
    println(s"[$this]sum in preRestart is $sum")
    println(s"[$this]message in preRestart is $message")
    self ! Resume(sum)
    super.preRestart(reason,message)
  }

  override def preStart():Unit = {
    println(s"[$this]sum in preStart is $sum")
  }

  override def postStop():Unit = {
    println(s"[$this]sum in postStop is ${sum}")
  }

  override def postRestart(reason: Throwable): Unit = {
    println(s"[$this]sum in postRestart is $sum")
    context become resuming

    super.postRestart(reason)
  }

  def resuming:Receive = {
    case Resume(s)⇒
      sum = s
      unstashAll()
      context become receive
    case _⇒
      stash()
  }

  override def receive = {
    case Error ⇒throw new ArithmeticException()
    case StopError ⇒ throw new Exception()
    case Increment ⇒
      sum = sum + 1
    case Print ⇒
      println(s"[$this]sum is $sum")
    case _⇒println("default msg")
  }
}

class Supervisor extends Actor {
  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries =  10,withinTimeRange = 1 minute) {
      case _:ArithmeticException ⇒ Restart
      case _:Exception ⇒ Stop
      case t ⇒ super.supervisorStrategy.decider.applyOrElse(t,(_:Any) ⇒ Escalate)
    }
  override def receive = {
    case (props:Props, name:String) ⇒
      sender ! context.actorOf(props,name)
    case StopActor(actorRef) ⇒context.stop(actorRef)
  }
}

object ActorLifeCycle extends App {
  implicit val timeout = Timeout(2 seconds)
  val actorSystem = ActorSystem("ClusterSystem")
  val supervisor = actorSystem.actorOf(Props[Supervisor],"supervisor")
  val childFuture = supervisor ? (Props(new LifeCycleActor),"LiftCycleActor")
  val child = Await.result(childFuture.mapTo[ActorRef],2 seconds)
  child ! Increment
  child ! Increment
  child ! Increment
  child ! Print
  child ! Error
  child ! Print

//  supervisor ! StopActor(child)
}
