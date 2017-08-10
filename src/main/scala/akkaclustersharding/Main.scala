package akkaclustersharding

import akka.actor.{ActorSystem, Props}
import akkaclustersharding.Devices
import com.typesafe.config.ConfigFactory

object ShardingApp  {
  def main(args: Array[String]): Unit = {
    startup(Seq("2551","2552","0"))
  }

  def startup(ports: Seq[String]): Unit = {
    ports foreach { port =>
      val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).withFallback(ConfigFactory.load())
      val system = ActorSystem("ClusterSystem", config)
      system.actorOf(Props[Devices])
    }
  }
}
