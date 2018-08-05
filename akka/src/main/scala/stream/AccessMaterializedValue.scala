package stream

import akka.stream.FlowShape
import akka.stream.scaladsl.{Flow, GraphDSL, Keep, Sink, Source}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object AccessMaterializedValue extends App {
  val flowFlow = Flow.fromGraph(GraphDSL.create(Sink.fold[Int, Int](0)(_ + _)) { implicit builder ⇒
    fold ⇒
      import GraphDSL.Implicits._
//      val mv = builder.materializedValue
//      mv.mapAsync(4)(identity)
      FlowShape(fold.in, builder.materializedValue.mapAsync(4)(identity).outlet)
  })

  val source = Source(0 to 100)
  val mat = source
    .viaMat(flowFlow)(Keep.right)
    .runWith(Sink.foreach(println))

  Await.ready(mat,Duration.Inf)
  system.terminate()

}
