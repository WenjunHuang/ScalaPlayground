package stream

import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object MapAsyncTest extends App {
  val source = Source((0 to 10).map { element ⇒
    if (element != 5)
      Future.successful(element)
    else
      Future.failed(new Exception("i failed"))
  })

  val f = source
    .mapAsync(4)(identity)
    .runWith(Sink.foreach(println))

  Await.ready(f, Duration.Inf)
  system.terminate()

}
