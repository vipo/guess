package akka.persistence.journal.mongo

import akka.persistence.journal.AsyncWriteJournal
import akka.persistence.PersistentRepr
import akka.actor.ActorContext
import scala.concurrent.Future
import scala.collection.immutable.Seq

class MongoJournal extends AsyncWriteJournal {

  def replayAsync(processorId: String, fromSequenceNr: Long, toSequenceNr: Long)(replayCallback: PersistentRepr => Unit): Future[Long] = ???

  def confirmAsync(processorId: String, sequenceNr: Long, channelId: String): Future[Unit] = ???

  def deleteAsync(processorId: String, fromSequenceNr: Long, toSequenceNr: Long,permanent: Boolean): Future[Unit] = ???

  def writeAsync(persistentBatch: Seq[PersistentRepr]): Future[Unit] = ???

}