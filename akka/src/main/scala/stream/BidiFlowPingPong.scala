package stream

import java.nio.ByteOrder

import akka.stream.BidiShape
import akka.stream.scaladsl.{BidiFlow, Flow, GraphDSL}
import akka.util.ByteString

object BidiFlowPingPong extends App {

  sealed trait Message

  case class Ping(id: Int) extends Message

  case class Pong(id: Int) extends Message

  def toBytes(msg: Message): ByteString = {
    implicit val ordr = ByteOrder.BIG_ENDIAN
    msg match {
      case Ping(id) ⇒ ByteString.newBuilder.putByte(1).putInt(id).result()
      case Pong(id) ⇒ ByteString.newBuilder.putByte(2).putInt(id).result()
    }
  }

  def fromBytes(bytes: ByteString): Message = {
    implicit val order = ByteOrder.BIG_ENDIAN
    val it = bytes.iterator
    it.getByte match {
      case 1 ⇒ Ping(it.getInt)
      case 2 ⇒ Pong(it.getInt)
      case other ⇒ throw new RuntimeException(s"parse error: expected 1|2 got $other")
    }
  }

  val codecVerbose = BidiFlow.fromGraph(GraphDSL.create() { implicit b ⇒
    val outbound = b.add(Flow[Message].map(toBytes))
    val inbound = b.add(Flow[ByteString].map(fromBytes))
    BidiShape.fromFlows(outbound, inbound)
  })

  val codec = BidiFlow.fromFunctions(toBytes, fromBytes)

}
