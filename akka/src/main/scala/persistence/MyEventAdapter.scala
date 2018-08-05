package persistence

import akka.actor.ExtendedActorSystem
import akka.persistence.journal.EventAdapter

class MyEventAdapter(system:ExtendedActorSystem) extends EventAdapter {
  override def manifest(event: Any) = ???

  override def toJournal(event: Any) = ???

  override def fromJournal(event: Any, manifest: String) = ???
}
