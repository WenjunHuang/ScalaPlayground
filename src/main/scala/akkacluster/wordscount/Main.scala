package akkacluster.wordscount

import akka.actor.{ActorSystem, Props}
import akka.cluster.Cluster
import com.typesafe.config.ConfigFactory

object Main extends App {
  val config = ConfigFactory.load()
  val system = ActorSystem("words", config)

  println(s"Starting node with roles: ${
    Cluster(system).selfRoles
  }")
  val roles = system.settings
    .config
    .getStringList("akka.cluster.roles")

  if (roles.contains("master")) {
    Cluster(system).registerOnMemberUp {
      val receptionist = system.actorOf(Props[JobReceptionist], "receptionist")
      println("Master node is ready.")
    }
  }

}
