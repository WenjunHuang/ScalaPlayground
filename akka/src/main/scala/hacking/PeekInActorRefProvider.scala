package hacking

import akka.actor.{ActorSystem, ExtendedActorSystem}

object PeekInActorRefProvider extends App {
  val system = ActorSystem("pirp").asInstanceOf[ExtendedActorSystem]
  val provider = system.provider

  println(provider.rootPath)

  system.terminate()

}
