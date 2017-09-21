package akkahttp

import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.io.StdIn

object WebServer extends App {
  implicit val system = ActorSystem("webserver")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val requestHandler: HttpRequest => HttpResponse = {
    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
      HttpResponse(entity = HttpEntity(
        ContentTypes.`text/html(UTF-8)`,
        "<html><body>Hello world!</body></html>"
      ))
    case HttpRequest(GET, Uri.Path("/ping"), _, _, _) =>
      HttpResponse(entity = "PONG!")
    case HttpRequest(GET, Uri.Path("/crash"), _, _, _) =>
      sys.error("BOOM!")
    case r: HttpRequest =>
      r.discardEntityBytes()
      HttpResponse(404, entity = "Unknown resource!")
  }

  val route =
    get {
      pathSingleSlash {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<html><body>Hello world</body></html>"))
      } ~
        path("ping") {
          complete("PONG!")
        } ~
        path("crash") {
          sys.error("BOOM!")
        }
    }
  val singleRoute: Route = { ctx =>
    if (ctx.request.method == HttpMethods.GET)
      ctx.complete("Received GET")
    else
      ctx.complete("Received something else")
  }
  val singleRouteWithDsl =
    get {
      complete("Received GET")
    } ~
      complete("Received something else")

  val routeWithCustomHeader = respondWithHeader(RawHeader("special-header", "you always have this even in 404")) {
    Route.seal(
      get {
        pathSingleSlash {
          complete {
            "Captain on the bridge!"
          }
        }
      }
    )
  }

  case class Color(name: String, red: Int, green: Int, blue: Int) {
    require(!name.isEmpty,"color name must not be empty")
    require(0 <= red && red <= 255, "red color component must be between 0 and 255")
  }

  val routeColor =
    (path("color" / Segment) & parameters('red.as[Int], 'green.as[Int], 'blue.as[Int])).as(Color) { color =>
      complete(s"name: ${color.name} red: ${color.red}, green: ${color.green}, blue: ${color.blue}")
    }

  val routeOrder: Route = path("order" / IntNumber) { id =>
    get {
      complete {
        "Received GET request for order " + id
      }
    } ~
      put {
        complete {
          "Received PUT request for order " + id
        }
      }
  }

  val getOrPut = get | put
  val routeAnother = (path("order" / IntNumber) & parameters('oem, 'expired ?)) { (id, oem, expired) =>
    getOrPut {
      extractMethod { m =>
        complete(s"Received ${m.name} request for order $id, oem: $oem, expired:$expired")
      }
    }
  }

  val routeAnd =
    (path("order" / IntNumber) & getOrPut & extractMethod & extractActorSystem) { (id, m, system) =>
      import akka.pattern._
      import scala.concurrent.duration._
      implicit val timeout = Timeout(5.seconds)
      val actorRef = system.actorOf(Props(classOf[EchoActor]))
      onComplete(actorRef ? s"Received ${m.name} request for order $id") { f =>
        complete(f.get.toString)
      }
    } ~
      pathSingleSlash {
        get {
          complete("root")
        }
      }

  val myExceptioHandler = ExceptionHandler {
    case _: ArithmeticException =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally")
        complete(HttpResponse(StatusCodes.InternalServerError, entity = "Bad numbers, bad result!!!"))
      }
  }

  val routeWithException =
    (path("compute") & parameters('one, 'two) & get) { (one, two) =>
      complete(s"${one.toInt / two.toInt}")
    }

  class EchoActor extends Actor {
    override def receive: Receive = {
      case msg =>
        sender ! msg
        context stop self
    }
  }

  //  val bindingFuture = Http().bindAndHandleSync(requestHandler, "0.0.0.0", 8080)
  val bindingFuture = Http().bindAndHandle(routeColor, "0.0.0.0", 8080)
  StdIn.readLine()

  bindingFuture.flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}

