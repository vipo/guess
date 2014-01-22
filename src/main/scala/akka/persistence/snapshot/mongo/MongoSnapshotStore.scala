package akka.persistence.snapshot.mongo

import akka.persistence.snapshot.SnapshotStore
import akka.persistence.SnapshotSelectionCriteria
import akka.persistence.SnapshotMetadata
import akka.persistence.SelectedSnapshot
import akka.actor.ActorContext

import scala.concurrent.Future

class MongoSnapshotStore extends SnapshotStore {

  def delete(processorId: String, criteria: SnapshotSelectionCriteria): Unit = ???

  def delete(metadata: SnapshotMetadata): Unit = ???

  def loadAsync(processorId: String, criteria: SnapshotSelectionCriteria): Future[Option[SelectedSnapshot]] = ???

  def saveAsync(metadata: SnapshotMetadata, snapshot: Any): Future[Unit] = ???

  def saved(metadata: SnapshotMetadata): Unit = ???

}