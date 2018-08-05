package persistence

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.{PersistentActor, Recovery, SnapshotOffer}
import com.typesafe.config.ConfigFactory

case class Cmd(data: String)

case class Evt(data: String)

case class ExampleState(events: List[String] = Nil) {
  def update(evt: Evt): ExampleState = copy(evt.data :: events)

  def size: Int = events.length

  override def toString: String = events.reverse.toString
}

class ExamplePersistenceActor extends PersistentActor with ActorLogging{
  var state = ExampleState()
  val snapShotInterval = 5

  override val receiveRecover = {
    case evt: Evt ⇒
      updateState(evt)
    case SnapshotOffer(_, snapshot: ExampleState) ⇒
      state = snapshot
  }

  def numEvents = state.size

  def updateState(evt: Evt) = {
    state = state.update(evt)
  }

  override def receiveCommand = {
    case Cmd(data) ⇒
      persist(Evt(s"${data}-${numEvents}")) { event ⇒
        updateState(event)
        context.system.eventStream.publish(event)
        if (lastSequenceNr % snapShotInterval == 0 && lastSequenceNr != 0)
          saveSnapshot(state)
      }
    case "print" ⇒ println(state)
  }

  override def onPersistFailure(cause: Throwable, event: Any, seqNr: Long): Unit =
    log.error(cause,event.toString)

  override def onPersistRejected(cause: Throwable, event: Any, seqNr: Long): Unit =
    log.error(cause,s"Rejected:${event}")

  override def persistenceId = "sample-id-1"

  override def recovery: Recovery = Recovery.none
}

object ExamplePersistenceActor{
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory
      .load("persistence-test")
      .getConfig("persistence")
      .withFallback(ConfigFactory.load())
    val system = ActorSystem("ExamplePersistence",config)
    val ref = system.actorOf(Props(classOf[ExamplePersistenceActor]))

    (1 to 10).foreach { i ⇒
      ref ! Cmd(s"command ${i}")
    }

    ref ! "print"
    Thread.sleep(200)

    (1 to 10).foreach { i ⇒
      ref ! Evt(s"command")
    }

    system.terminate()
  }
}
