package akkatyped

import akka.actor.ActorSystem
import akka.typed.Behavior
import akka.typed.scaladsl.Actor
import akka.typed.scaladsl.adapter._

object MyUntype1 {
  def props(): akka.actor.Props = akka.actor.Props(new MyUntype1)
}

class MyUntype1 extends akka.actor.Actor {
  val second: akka.typed.ActorRef[MyTyped1.Command] = context.spawn(MyTyped1.behavior, "second")

  context.watch(second)

  second ! MyTyped1.Ping(self)

  override def receive: Receive = {
    case MyTyped1.Pong ⇒
      println(s"$self got Pong from ${sender()}")
      context.stop(second)
    case akka.actor.Terminated(ref) ⇒
      println(s"$self observed termination of $ref")
      context.stop(self)
  }
}

object MyTyped1 {

  sealed trait Command

  final case class Ping(replyTo: akka.typed.ActorRef[Pong.type]) extends Command

  case object Pong

  val behavior: Behavior[Command] =
    akka.typed.scaladsl.Actor.immutable { (ctx, msg) ⇒
      msg match {
        case Ping(replyTo) ⇒
          println(s"${ctx.self} got Ping from $replyTo")
          replyTo ! Pong
          Actor.same
      }
    }
}

object MyTyped2 {

  final case class Ping(replyTo: akka.typed.ActorRef[Pong.type])

  sealed trait Command

  case object Pong extends Command

  val behavior: Behavior[Command] =
    akka.typed.scaladsl.Actor.deferred { ctx ⇒
      val second: akka.actor.ActorRef = ctx.actorOf(MyUntyped2.props(), "second")

      ctx.watch(second)

      second.tell(MyTyped2.Ping(ctx.self),ctx.self.toUntyped)

      akka.typed.scaladsl.Actor.immutable[Command]{(ctx,msg)⇒
        msg match {
          case Pong ⇒
            println(s"${ctx.self} got Pong")
            ctx.stop(second)
            Actor.same
        }
      } onSignal {
        case (ctx,akka.typed.Terminated(ref)) ⇒
          println(s"${ctx.self} observed termination of $ref")
          Actor.stopped
      }
    }
}

object MyUntyped2{
  def props(): akka.actor.Props = akka.actor.Props(new MyUntyped2)
}

class MyUntyped2 extends akka.actor.Actor {
  override def receive: Receive = {
    case MyTyped2.Ping(replyTo) ⇒
      println(s"$self got Pong from ${sender()}")
      replyTo ! MyTyped2.Pong
  }
}
object TypeAndUntyped extends App {
  val system = ActorSystem("TypeAndUntyped")
  system.actorOf(MyUntype1.props())
}