package akkaclustersharding

import akka.actor.{Actor, ActorLogging}

object Device {

  case class RecordTemperature(deviceId: Int, temperature: Double)

}

class Device extends Actor with ActorLogging {

  import Device._

  override def receive: Receive = counting(Nil)

  def counting(values: List[Double]): Receive = {
    case RecordTemperature(id, temp) =>
      val temperatures = temp :: values
      log.info(s"Recording temperature $temp for device $id, average is ${temperatures.sum / temperatures.size} after ${temperatures.size} reading")
      context.become(counting(temperatures))
  }
}
