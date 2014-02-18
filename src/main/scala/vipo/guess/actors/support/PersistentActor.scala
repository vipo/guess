package vipo.guess.actors.support

import akka.persistence.Processor
import scala.concurrent.duration._
import akka.actor.ActorLogging
import akka.persistence.SaveSnapshotFailure
import akka.persistence.SaveSnapshotSuccess
import akka.persistence.SnapshotOffer

abstract class PersistentActor[T] extends Processor with ActorLogging with ActorSystemSettings {

  protected val SnapMessage = "snap"
    
  override def preStart() =
    try super.preStart
    finally scheduleSnapshot(SnapshotDurationInitial)

  def receive = {
    case SaveSnapshotSuccess(metadata) =>
      try scheduleSnapshot()
      finally log.info(s"Snapshot saved $metadata")
    case SaveSnapshotFailure(metadata, reason) =>
      log.error(s"Snapshot $metadata failed: $reason")
    case SnapshotOffer(metadata, data) =>
      try loadData(data.asInstanceOf[T])
      finally log.info(s"Recovered from snapshot: $metadata")
    case SnapMessage =>
      saveSnapshot(dataForSave)
  }

  def loadData(data:T): Unit;
  def dataForSave(): T;

  private def scheduleSnapshot(d: FiniteDuration = SnapshotDuration) =
    if (!SnapshotsDisabled)
      context.system.scheduler.scheduleOnce(d, self, SnapMessage)

}