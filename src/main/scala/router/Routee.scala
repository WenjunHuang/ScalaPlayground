package router

import akka.actor.{Actor, ActorContext, ActorRefFactory, Props, Terminated}
import akka.routing.{ActorRefRoutee, Routee, Router, RoutingLogic}

import scala.collection.immutable

class FooActor() extends Actor {
  override def receive: Receive = {
    case "ping" =>
      sender().tell("pong", context.parent)
      context stop self
  }
}

case class CreateNewRoutingLogic(context: ActorContext, routeeProps: Props) extends RoutingLogic {
  override def select(message: Any, routees: immutable.IndexedSeq[Routee]): Routee = {
    val ref = context.actorOf(routeeProps)
    context.watch(ref)
    ActorRefRoutee(ref)
  }
}

class MasterActor(routeeProps: Props) extends Actor {
  val router = Router(CreateNewRoutingLogic(context, routeeProps))

  override def receive: Receive = {
    case Terminated(name) =>
      println(s"$name is terminated")
    case it =>
      router.route(it, sender())
  }
}

