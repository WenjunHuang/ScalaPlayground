package stream
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._

object SimpleHttp extends App {
  val route = path("simple") {
    get {
      complete("OK")
    }
  }

  val binding = Http().bindAndHandle(route,"0.0.0.0",9090)

}
