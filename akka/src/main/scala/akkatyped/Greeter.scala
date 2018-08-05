package akkatyped

import akka.typed.ActorSystem
import akka.typed.{ActorRef, Behavior}
import akka.typed.scaladsl.Actor


object Greeter {

  sealed trait Command

  case object Greet extends Command

  final case class WhoToGreet(who: String) extends Command

  val greeterBehavior: Behavior[Command] = Actor.mutable[Command](ctx⇒new Greeter)
}

class Greeter extends Actor.MutableBehavior[Greeter.Command] {
  import Greeter._

  private var greeting = "hello"
}