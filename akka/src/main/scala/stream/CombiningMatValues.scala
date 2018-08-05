package stream

import akka.actor.Cancellable
import akka.stream.ClosedShape
import akka.stream.scaladsl.{Flow, GraphDSL, Keep, RunnableGraph, Sink, Source}

import scala.concurrent.{Future, Promise}

object CombiningMatValues extends App {
  implicit val ec = system.dispatcher
  val source: Source[Int, Promise[Option[Int]]] = Source.maybe[Int]
  val flow: Flow[Int, Int, Cancellable] = ???
  val sink: Sink[Int, Future[Int]] = Sink.head[Int]

  val r1: RunnableGraph[Promise[Option[Int]]] = source.via(flow).to(sink)
  val r2: RunnableGraph[Cancellable] = source.viaMat(flow)(Keep.right).to(sink)
  val r3: RunnableGraph[Future[Int]] = source.via(flow).toMat(sink)(Keep.right)

  val r4: Future[Int] = source.via(flow).runWith(sink)
  val r5: Promise[Option[Int]] = flow.to(sink).runWith(source)
  val r6: (Promise[Option[Int]], Future[Int]) = flow.runWith(source, sink)
  val r7: RunnableGraph[(Promise[Option[Int]], Cancellable)] = source.viaMat(flow)(Keep.both).to(sink)

  val r8: RunnableGraph[(Promise[Option[Int]], Future[Int])] =
    source.via(flow).toMat(sink)(Keep.both)

  val r9:RunnableGraph[((Promise[Option[Int]],Cancellable),Future[Int])] = source.viaMat(flow)(Keep.both).toMat(sink)(Keep.both)

  val r10:RunnableGraph[(Cancellable,Future[Int])] = source.viaMat(flow)(Keep.right).toMat(sink)(Keep.both)

  val r11:RunnableGraph[(Promise[Option[Int]], Cancellable, Future[Int])] = r9.mapMaterializedValue{
    case ((promise, cancellable), future)⇒
      (promise,cancellable,future)
  }


  val (promise, cancellable, future) = r11.run()

  promise.success(None)
  cancellable.cancel()
  future.map(_+3)


  val r12 = RunnableGraph.fromGraph(GraphDSL.create(source,flow,sink)((_,_,_)) { implicit builder ⇒ (src, f, dst) ⇒
    import GraphDSL.Implicits._
    src ~> f ~> dst
    ClosedShape
  })
}
