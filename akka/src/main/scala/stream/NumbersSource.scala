package stream

import akka.stream.scaladsl.Source
import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}
import akka.stream.{Attributes, Outlet, SourceShape}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class NumbersSource extends GraphStage[SourceShape[Int]] {
  override def createLogic(inheritedAttributes: Attributes) =  new GraphStageLogic(shape){
    private var counter = 1

    setHandler(out, new OutHandler{
      override def onPull(): Unit = {
        push(out,counter)
        counter += 1
      }

      override def onDownstreamFinish(): Unit = {

      }
    })
  }

  override def shape = SourceShape(out)

  val out:Outlet[Int] = Outlet("NumbersSource")
}


object NumbersSource extends App{
  val sourceGraph = new NumbersSource
  val mySource = Source.fromGraph(sourceGraph)

  val result1 = mySource.take(10).runFold(0)(_+_)
  val result2 = mySource.take(100).runFold(0)(_+_)

  println(Await.result(result1,Duration.Inf))
  println(Await.result(result2,Duration.Inf))

}