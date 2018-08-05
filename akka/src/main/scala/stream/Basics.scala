package stream

import akka.Done
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object Basics extends App {
  implicit val ec = system.dispatcher
  val source = Source(1 to 10)
  val sink = Flow[Int]
    .alsoTo(Flow[Int].map(_.toString).to(Sink.foreach[String](println(_))))
    .toMat(Sink.fold(0)(_ + _))(Keep.right)

  var futures: List[Future[Int]] = Nil
  (1 to 10).foreach { _ ⇒
    futures = source.runWith(sink) +: futures
  }

  val f = Future.traverse(futures) { v ⇒ v }
  val l = Await.result(f, Duration.Inf)
  println(l)
  system.terminate()

}
