package akkaeventsourcing

import akka.persistence.PersistentActor

class BookActor(id:Int) extends PersistentActor {
  override def receiveRecover: Receive = ???

  override def receiveCommand: Receive = ???

  override def persistenceId: String = s"book-$id"
}
