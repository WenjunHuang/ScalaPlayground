package Bug

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import com.typesafe.config.ConfigFactory

import scala.util.Failure

object AppConfig {
  val actorSystemConfig = ConfigFactory.parseString(
    """
      akka {
        loglevel = DEBUG
        loggers = ["akka.event.slf4j.Slf4jLogger"]
    }
    """
  )
}

object ReceiverApp extends App {

  implicit val system = ActorSystem("actor-system", AppConfig.actorSystemConfig)
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings.create(system).withDebugLogging(true))(system)
  import system.dispatcher

  val route: Route =
    (path("putData") & put) {
      extractRequestEntity { entity =>
        complete {
          entity.discardBytes()
          StatusCodes.OK
        }
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8081)
  System.in.read()
}

object ProxyApp extends App {

  implicit val system = ActorSystem("actor-system", AppConfig.actorSystemConfig)
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings.create(system).withDebugLogging(true))(system)
  import system.dispatcher

  val route: Route =
    (path("postData") & post) {
      extractRequestEntity { entity =>
        complete {
          Http().singleRequest(
            HttpRequest(
              method = HttpMethods.PUT,
              uri    = Uri("http://localhost:8081/putData"),
              entity = entity
            )
          ).andThen {
            case Failure(ex) =>
              println("sending request to receiver failed" + ex)
              ex.printStackTrace()
          }
        }
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
  System.in.read()
}

object ReproducerClientApp extends App {
  implicit val system = ActorSystem("actor-system", AppConfig.actorSystemConfig)
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings.create(system).withDebugLogging(true))(system)

  Source.fromIterator(() => Iterator.from(0))
    .mapAsync(30) { index =>
      Http().singleRequest(
        HttpRequest(
          method = HttpMethods.POST,
          uri    = Uri("http://localhost:8080/postData"),
          entity = "Data" * index
        )
      )
    }.map { r => println(s"send request ${r.status}"); r.discardEntityBytes() }.runWith(Sink.ignore)

}
