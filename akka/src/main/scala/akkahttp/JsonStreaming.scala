package akkahttp

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.common.EntityStreamingSupport
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.Source
import akkahttp.JsonStreaming.{Measurement, Tweet}
import spray.json.DefaultJsonProtocol

object JsonStreaming extends App {
  implicit val system = ActorSystem("JsonStreaming")

  case class Tweet(uid: Int, txt: String)

  case class Measurement(id: String, value: Int)

  implicit val jsonStreamingSupport = EntityStreamingSupport.json()
  val route =
    path("tweets") {
      complete("")
    }

}

object MyJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val tweetFormat = jsonFormat2(Tweet.apply)
  implicit val measurementFormat = jsonFormat2(Measurement.apply)
}
