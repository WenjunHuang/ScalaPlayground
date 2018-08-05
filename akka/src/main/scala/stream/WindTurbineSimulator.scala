package stream

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.model.ws.{Message, TextMessage, WebSocketRequest, WebSocketUpgradeResponse}
import akka.stream.{ActorMaterializer, FlowShape, KillSwitches, SourceShape}
import akka.stream.scaladsl.{Flow, GraphDSL, Keep, Sink, Source}
import stream.WindTurbineSimulator._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Random, Success}

class WindTurbineSimulatorException(val id:String) extends Exception{}

object WindTurbineSimulator {
  def props(id: String, endpoint: String)(implicit materializer: ActorMaterializer) =
    Props(classOf[WindTurbineSimulator], id, endpoint, materializer)

  final case object Upgraded

  final case object Connected

  final case object Terminated

  final case class ConnectionFailure(ex: Throwable)

  final case class FailedUpgrade(statusCode: StatusCode)


}

class WindTurbineSimulator(id: String, endpoint: String)(implicit materializer: ActorMaterializer)
  extends Actor with ActorLogging {
  implicit private val system = context.system
  implicit private val ec = system.dispatcher

  val webSocket = WebSocketClient(id, endpoint, self)

  override def postStop(): Unit = {
    log.info(s"$id : Stopping WebSocket connection")
    webSocket.killSwitch.shutdown()
  }

  override def receive = {
    case Upgraded ⇒
      log.info(s"$id: WebSocket upgraded")
    case FailedUpgrade(statusCode) ⇒
      log.error(s"$id: Failed to upgrade WebSocket connection : $statusCode")
      throw new WindTurbineSimulatorException(id)
    case ConnectionFailure(ex) ⇒
      log.error(s"$id: Failed to establish WebSocket connection $ex")
      throw new WindTurbineSimulatorException(id)
    case Connected ⇒
      log.info(s"$id: WebSocket connected")
      context.become(running)
  }

  def running: Receive = {
    case Terminated ⇒
      log.error(s"$id: WebSocket connection terminated")
      throw new WindTurbineSimulatorException(id)
  }
}

object WebSocketClient {
  def apply(id: String, endpoint: String, supervisor: ActorRef)(implicit system : ActorSystem,
                                                         materializer    : ActorMaterializer,
                                                         executionContext: ExecutionContext) = {
    new WebSocketClient(id, endpoint, supervisor)(system, materializer, executionContext)
  }
}

class WebSocketClient(id: String, endpoint: String, supervisor: ActorRef)(implicit
                                                                          system          : ActorSystem,
                                                                          materializer    : ActorMaterializer,
                                                                          executionContext: ExecutionContext){
  val webSocket: Flow[Message,Message,Future[WebSocketUpgradeResponse]] = {
    val websocketUrl = s"$endpoint/$id"
    Http().webSocketClientFlow(WebSocketRequest(websocketUrl))
  }

  val outgoing = GraphDSL.create(){implicit builder ⇒
    val data = WindTurbineData(id)
    val flow = builder.add {
      Source.tick(1 seconds, 1 seconds, ())
        .map(_⇒TextMessage(data.getNext))
    }

    SourceShape(flow.out)
  }

  val incoming = GraphDSL.create(){implicit builder ⇒
    val flow = builder.add {
      Flow[Message]
        .collect {
          case TextMessage.Strict(text)⇒
            Future.successful(text)
          case TextMessage.Streamed(textStream)⇒
            textStream.runFold("")(_+_)
              .flatMap(Future.successful)
        }
        .mapAsync(1)(identity)
        .map(println)
    }
    FlowShape(flow.in,flow.out)
  }

  val ((upgradeResponse,killSwitch),closed) = Source.fromGraph(outgoing)
    .viaMat(webSocket)(Keep.right)
    .viaMat(KillSwitches.single)(Keep.both)
    .via(incoming)
    .toMat(Sink.ignore)(Keep.both)
    .run()

  val connected = upgradeResponse.map{upgrade ⇒
    upgrade.response.status match {
      case StatusCodes.SwitchingProtocols⇒supervisor ! Upgraded
      case statusCode ⇒ supervisor ! FailedUpgrade(statusCode)
    }
  }

  connected.onComplete {
    case Success(_)⇒supervisor ! Connected
    case Failure(ex) ⇒ supervisor ! ConnectionFailure(ex)
  }

  closed.map{_⇒
    supervisor ! Terminated
  }

}

object WindTurbineData {
  def apply(id: String) = new WindTurbineData(id)
}

class WindTurbineData(id:String) {
  val random = Random

  def getNext:String = {
    val timestamp = System.currentTimeMillis() / 1000
    val power = f"${random.nextDouble() * 10}%.2f"
    val rotorSpeed = f"${random.nextDouble() * 10}%.2f"
    val windSpeed = f"${random.nextDouble() * 100}%.2f"

    s"""{
       |  "id":"$id",
       |  "timestamp":$timestamp,
       |  "measurements":{
       |    "power":$power,
       |    "roter_speed":$rotorSpeed,
       |    "wind_speed":$windSpeed
       |  }
       |}
     """.stripMargin
  }
}

object SimulateWindTurbines extends App {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  (1 to 1000).foreach{_⇒
    val id = UUID.randomUUID().toString
  }
}