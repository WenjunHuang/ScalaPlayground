package akkasupervision

import akka.actor.{Actor, ActorSystem, Props}

object ParentChild extends App {
  val actorSystem = ActorSystem("ClusterSystem")
  val parent = actorSystem.actorOf(Props[ParentActor],"parent")
  parent ! CreateChild
}
class ParentActor extends Actor {
  def receive = {
    case CreateChild ⇒
      val child = context.actorOf(Props[ChildActor],"child")
      child ! Greet("Hello Child")
  }

}

class ChildActor extends Actor{
  def receive = {
    case Greet(msg) ⇒ println(s"My parent[${self.path.parent} greeted to me [${self.path}] $msg")
  }
}

case object CreateChild
case class Greet(msg:String)
