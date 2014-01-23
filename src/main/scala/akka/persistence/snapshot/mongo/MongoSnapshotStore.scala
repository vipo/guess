package akka.persistence.snapshot.mongo

import akka.persistence.snapshot.SnapshotStore
import akka.persistence.SnapshotSelectionCriteria
import akka.persistence.SnapshotMetadata
import akka.persistence.SelectedSnapshot
import akka.actor.ActorContext
import scala.concurrent.Future
import reactivemongo.api.MongoDriver

class MongoSnapshotStore extends SnapshotStore {

  implicit val executionContext = context.system.dispatcher
  val driver = new MongoDriver(context.system)
  val connection = driver.connection("localhost:27017" :: Nil)
  val db = connection.db("guess")
  val collection = db.collection("snapshots")
  
  def delete(processorId: String, criteria: SnapshotSelectionCriteria): Unit = ???

  def delete(metadata: SnapshotMetadata): Unit = ???

  def loadAsync(processorId: String, criteria: SnapshotSelectionCriteria): Future[Option[SelectedSnapshot]] = ???

  def saveAsync(metadata: SnapshotMetadata, snapshot: Any): Future[Unit] = ???

  def saved(metadata: SnapshotMetadata): Unit = ???

}