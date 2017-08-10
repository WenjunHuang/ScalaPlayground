package cats

import cats._
import cats.data._
import cats.implicits._

sealed trait TrafficLight

object TrafficLight {
  implicit def red: TrafficLight = Red

  implicit def green: TrafficLight = Green

  implicit def yellow: TrafficLight = Yellow

  case object Red extends TrafficLight

  case object Yellow extends TrafficLight

  case object Green extends TrafficLight

}

object Day1 extends App {


  implicit val trafficLightEq: Eq[TrafficLight] = (a1: TrafficLight, a2: TrafficLight) => a1 == a2

}
