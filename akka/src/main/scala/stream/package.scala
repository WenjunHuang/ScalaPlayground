import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

package object stream {
  implicit val system = ActorSystem("StreamAS")
  implicit val materializer = ActorMaterializer()

}
