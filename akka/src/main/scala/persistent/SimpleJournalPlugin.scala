package persistent

import akka.persistence.{AtomicWrite, PersistentRepr}
import akka.persistence.journal.AsyncWriteJournal

import scala.collection.immutable
import scala.concurrent.Future
import scala.util.{Success, Try}

class SimpleJournalPlugin extends AsyncWriteJournal{
  override def asyncWriteMessages(messages: immutable.Seq[AtomicWrite]) = {
    println(s"asyncWriteMessages $messages")
    Future.successful(List(Try()))
  }

  override def asyncDeleteMessagesTo(persistenceId: String, toSequenceNr: Long) = {
    println(s"asyncDeleteMessagesTo $persistenceId, $toSequenceNr")
    Future.successful()
  }

  override def asyncReplayMessages(persistenceId: String,
  fromSequenceNr: Long,
  toSequenceNr: Long,
  max: Long)(recoveryCallback: PersistentRepr ⇒ Unit) = {
    println(s"asyncReplayMessages $persistenceId, $fromSequenceNr, $toSequenceNr, $max")
    Future.successful()
  }

  override def asyncReadHighestSequenceNr(persistenceId: String, fromSequenceNr: Long) = {
    println(s"asyncReadHighestSequenceNr $persistenceId, $fromSequenceNr")
    Future.successful(1L)
  }
}
