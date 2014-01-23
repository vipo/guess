package vipo.guess.actors

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.persistence.Processor
import akka.persistence.Persistent
import akka.persistence.SnapshotOffer
import akka.persistence.SaveSnapshotFailure
import akka.persistence.SaveSnapshotSuccess
import vipo.guess.domain.Language._
import vipo.guess.domain.Challenge._
import scala.concurrent.duration._
import scala.concurrent.duration.Duration

case class SampleGenerated(val langNo: LangNo)
case class GetSampleGeneratedTimes(val langNo: LangNo)

object StatisticsActor {
  type SampleGenData = Map[LangNo, Long]
  type ChallengeData = Map[ChallengeId, Long]
  val SnapshotDuration = 10 hours
  val SnapshotDurationInitial = 10 seconds
}

@SerialVersionUID(42L)
case class SnapshotData(
  val sampleGenData: StatisticsActor.SampleGenData,
  val challengeData: StatisticsActor.ChallengeData) extends Serializable

class StatisticsActor extends Processor with ActorLogging {
  import StatisticsActor._
  
  private val snapMessage = "snap"
  private var langGenerated: SampleGenData = Map().withDefaultValue(0)
  private var challengesTried: ChallengeData = Map().withDefaultValue(0)

  override def preStart() =
    try super.preStart
    finally scheduleSnapshot(SnapshotDurationInitial)

  def receive = {
    case Persistent(SampleGenerated(no), _) => langGenerated =
      langGenerated + (no -> (langGenerated(no) + 1))
    case msg@SampleGenerated(_) => self forward Persistent(msg)
    case GetSampleGeneratedTimes(no) => sender ! langGenerated(no)
    //persistence
    case SnapshotOffer(metadata, SnapshotData(g, c)) =>
      try {langGenerated = g; challengesTried = c}
      finally log.info(s"Recovered from snapshot: $metadata")
    case s if s == snapMessage =>
      saveSnapshot(SnapshotData(langGenerated, challengesTried))
    case SaveSnapshotSuccess(metadata) =>
      try scheduleSnapshot()
      finally log.info(s"Snapshot saved $metadata")
    case SaveSnapshotFailure(metadata, reason) =>
      log.error(s"Snapshot $metadata failed: $reason")
    case msg => log.error(s"received unknown message: $msg")
  }

  private def scheduleSnapshot(d: FiniteDuration = SnapshotDuration) = {
    import vipo.guess.Bootstrap._
    system.scheduler.scheduleOnce(d, self, snapMessage)
  }
}