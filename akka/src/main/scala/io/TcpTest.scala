package io

import akka.actor.ActorSystem
import akka.io.{IO, Tcp}

object TcpTest extends App {
  implicit val system  =ActorSystem("TcpTest")
  val manager = IO(Tcp)

}
