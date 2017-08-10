package akkaclustersharding

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}

import scala.concurrent.ExecutionContext
import scala.util.Random
import scala.concurrent.duration._

object Devices {
  case object UpdateDevice
}

class Devices extends Actor with ActorLogging {
  import Devices._

  val extractEntityId: ShardRegion.ExtractEntityId = {
    case msg @ Device.RecordTemperature(id, _) => (id.toString, msg)
  }

  val extractShardId: ShardRegion.ExtractShardId = {
    case Device.RecordTemperature(id, _) => (id % numberOfShards).toString
  }
  val numberOfShards = 100

  val deviceRegion:ActorRef = ClusterSharding(context.system).start(
    typeName = "Device",
    entityProps = Props[Device],
    settings = ClusterShardingSettings(context.system),
    extractEntityId = extractEntityId,
    extractShardId = extractShardId)

  val random = new Random()
  val numberOfDevices = 50

  implicit val ec:ExecutionContext = context.dispatcher
  context.system.scheduler.schedule(10.seconds, 1.second, self,UpdateDevice)

  override def receive: Receive = {
    case UpdateDevice =>
      val deviceId = random.nextInt(numberOfDevices)
      val temperature = 5 + 30 * random.nextDouble()
      val msg = Device.RecordTemperature(deviceId, temperature)
      log.info(s"Sending $msg")
      deviceRegion ! msg
  }
}
