package vipo.guess.actors

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.persistence.Processor
import akka.persistence.Persistent
import akka.persistence.SnapshotOffer
import akka.persistence.SaveSnapshotFailure
import akka.persistence.SaveSnapshotSuccess

case class SampleGenerated(val langNo: Int)
case class GetSampleGeneratedTimes(val langNo: Int)

class StatisticsActor extends Processor with ActorLogging {

  private type Data = Map[Int, Long]
  private val snapMessage = "snap"
  private var langGenerated: Data = Map().withDefaultValue(0)

  override def preStart() = try super.preStart finally scheduleSnapshot

  def receive = {
    case Persistent(SampleGenerated(no), _) => langGenerated =
      langGenerated + (no -> (langGenerated(no) + 1))
    case SnapshotOffer(metadata, offeredSnapshot) =>
      try langGenerated = offeredSnapshot.asInstanceOf[Data]
      finally log.debug(s"Recovered from snapshot: $metadata")
    case s if s == snapMessage => saveSnapshot(langGenerated)
    case msg@SampleGenerated(_) => self forward Persistent(msg)
    case GetSampleGeneratedTimes(no) => sender ! langGenerated(no)
    case SaveSnapshotSuccess(metadata) =>
      try scheduleSnapshot
      finally log.debug(s"Snapshot saved $metadata")
    case SaveSnapshotFailure(metadata, reason) => log.error(s"Snapshot $metadata failed", reason)
    case msg@_ => log.error(s"received unknown message: $msg")
  }

  private def scheduleSnapshot = {
    import vipo.guess.Bootstrap._
    import scala.concurrent.duration._
    system.scheduler.scheduleOnce(4 hours, self, snapMessage)
  }
}