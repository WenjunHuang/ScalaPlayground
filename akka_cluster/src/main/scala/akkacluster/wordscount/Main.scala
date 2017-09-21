package akkacluster.wordscount

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

object Main {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Test")
    val receptionist = system.actorOf(Props[JobReceptionist], "receptionist")
    println("Master node is ready.")
  }

  def startup(ports: Seq[String]): Unit = {
    ports foreach { port =>
      val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).withFallback(ConfigFactory.load())
      val system = ActorSystem("ClusterSystem", config)
      if (port == "2551") {
        val receptionist = system.actorOf(Props[JobReceptionist], "receptionist")
        println("Master node is ready.")
      }
    }
  }

}
