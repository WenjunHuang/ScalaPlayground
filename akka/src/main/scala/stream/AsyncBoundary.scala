package stream

import akka.stream.scaladsl.{Keep, Sink, Source}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object AsyncBoundary extends App {
  val r = Source(1 to 100000000)
    .async
    .filterNot { i ⇒
      (2 to math.sqrt(i.toDouble).toInt).exists { d ⇒ i % d == 0 }
    }
    .async
    .alsoToMat(Sink.fold(0) { (acc, _) ⇒ acc + 1 })(Keep.right)

  val mat = Sink.foreach(println).runWith(r)

  val count = Await.result(mat, Duration.Inf)
  println(s"total prime count: $count")

  sys.addShutdownHook(system.terminate())
}
