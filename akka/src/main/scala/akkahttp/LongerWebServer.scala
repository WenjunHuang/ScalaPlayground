package akkahttp

import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.Credentials
import akka.stream.ActorMaterializer
import akka.util.Timeout
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.concurrent.duration._

object LongerWebServer {
  type Money = Double
  type TransactionResult = String
  case class User(name:String)
  case class Order(email:String, amount:Money)
  case class Update(order:Order)
  case class OrderItem(i:Int, os:Option[String],s:String)

  implicit val orderFormat = jsonFormat2(Order)
  implicit val timeout:Timeout = 5.seconds


  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher
  }

}
