package stream

import akka.NotUsed
import akka.stream.FanInShape.{Init, Name}
import akka.stream.scaladsl.{Balance, Flow, GraphDSL, Merge, MergePreferred, RunnableGraph, Sink, Source}
import akka.stream._

object PriorityWorkerPoolShapeMain extends App{
  val worker1 = Flow[String].map("step 1 " + _)
  val worker2 = Flow[String].map("step 2 " + _)

  RunnableGraph.fromGraph(GraphDSL.create(){implicit b ⇒
    import GraphDSL.Implicits._

    val priorityPool1 = b.add(PriorityWorkerPool(worker1,4))
    val priorityPool2 = b.add(PriorityWorkerPool(worker2,2))

    Source(1 to 100).map("job: " + _) ~> priorityPool1.jobsIn
    Source(1 to 100).map("priority job: " + _) ~> priorityPool1.priorityJobsIn

    priorityPool1.resultsOut ~> priorityPool2.jobsIn
    Source(1 to 100).map("one-step, priority " + _) ~> priorityPool2.priorityJobsIn

    priorityPool2.resultsOut ~> Sink.foreach(println)
    ClosedShape
  }).run()
}

case class PriorityWorkerPoolShape[In, Out](jobsIn: Inlet[In],
                                            priorityJobsIn: Inlet[In],
                                            resultsOut: Outlet[Out]) extends Shape {
  override def inlets = jobsIn :: priorityJobsIn :: Nil

  override def outlets = resultsOut :: Nil

  override def deepCopy() = PriorityWorkerPoolShape(jobsIn.carbonCopy(),
                                                    priorityJobsIn.carbonCopy(),
                                                    resultsOut.carbonCopy())
}

class PriorityWorkerPoolShape2[In,Out](_init:Init[Out] = Name("PriorityWorkerPool")) extends FanInShape[Out](_init){
  override protected def construct(init: Init[Out]) = new PriorityWorkerPoolShape2(init)

  val jobsIn = newInlet[In]("jobsIn")
  val priorityJobsIn = newInlet[In]("priorityJobsIn")
}

object PriorityWorkerPool {
  def apply[In,Out](worker:Flow[In,Out,Any],
                    workerCount:Int):Graph[PriorityWorkerPoolShape[In,Out],NotUsed] = {
    GraphDSL.create(){implicit b⇒
      import GraphDSL.Implicits._

      val priorityMerge = b.add(MergePreferred[In](1))
      val balance = b.add(Balance[In](workerCount))
      val resultsMerge = b.add(Merge[Out](workerCount))

      priorityMerge ~> balance
      for (i ← 0 until workerCount)
        balance.out(i) ~> worker ~> resultsMerge.in(i)

      PriorityWorkerPoolShape(jobsIn = priorityMerge.in(0),
                              priorityJobsIn = priorityMerge.preferred,
                              resultsOut = resultsMerge.out)
    }
  }
}