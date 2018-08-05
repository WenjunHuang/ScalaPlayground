package stream

import akka.stream.scaladsl.{GraphDSL, RunnableGraph, Sink, Source, ZipWith}
import akka.stream.{ClosedShape, UniformFanInShape}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object PickMaxOfThree extends App {

  val pickMaxOfThree = GraphDSL.create() { implicit b ⇒
    import GraphDSL.Implicits._

    val zip1 = b.add(ZipWith[Int, Int, Int](math.max))
    val zip2 = b.add(ZipWith[Int, Int, Int](math.max))

    zip1.out ~> zip2.in0

    UniformFanInShape(zip2.out, zip1.in0, zip1.in1, zip2.in1)
  }

  val resultSink = Sink.head[Int]

  val g = RunnableGraph.fromGraph(GraphDSL.create(resultSink) { implicit b ⇒
    sink ⇒
      import GraphDSL.Implicits._
      val pm3 = b.add(pickMaxOfThree)

      Source.single(1) ~> pm3.in(0)
      Source.single(2) ~> pm3.in(1)
      Source.single(3) ~> pm3.in(2)
      pm3.out ~> sink.in
      ClosedShape
  })

  val max:Future[Int] = g.run()
  println(Await.result(max, 300.millis))

}
