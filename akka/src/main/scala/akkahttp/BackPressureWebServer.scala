package akkahttp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.http.scaladsl.server.Directives._
import akka.util.ByteString

import scala.io.StdIn
import scala.util.Random

object BackPressureWebServer {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val numbers = Source.fromIterator(()=>Iterator.continually(Random.nextInt()))
    val route = path("random") {
      get {
        complete(HttpEntity(
          ContentTypes.`text/plain(UTF-8)`,
          numbers.map(n=>ByteString(s"$n\n"))
        ))
      }
    }

    val bindingFuture = Http().bindAndHandle(route,"0.0.0.0",8080)
    StdIn.readLine()
    bindingFuture.flatMap(_.unbind())
      .onComplete(_=>system.terminate())
  }

}
