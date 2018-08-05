package stream

import akka.stream.{Attributes, Inlet, SinkShape}
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler}

class StdoutSink extends GraphStage[SinkShape[Int]] {
  val in:Inlet[Int] = Inlet("StdoutSink")

  override def createLogic(inheritedAttributes: Attributes) = new GraphStageLogic(shape) {
    override def preStart(): Unit = pull(in)

    setHandler(in, new InHandler {
      override def onPush(): Unit = {
        println(grab(in))
        pull(in)
      }
})
  }

  override def shape = SinkShape(in)
}
