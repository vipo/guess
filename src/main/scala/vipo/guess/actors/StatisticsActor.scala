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
  val SnapshotDuration = 10 hours
  val SnapshotDurationInitial = 10 seconds
}

class StatisticsActor extends Processor with ActorLogging {
  import StatisticsActor._
  
  private type SampleGenData = Map[LangNo, Long]
  private type ChallengeData = Map[ChallengeId, Long]
  private type StatisticsSnapshotData = Tuple2[SampleGenData, ChallengeData]
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
    case SnapshotOffer(metadata, snapshot) if snapshot.isInstanceOf[StatisticsSnapshotData]=> {
      val s: StatisticsSnapshotData = snapshot.asInstanceOf[StatisticsSnapshotData]
      langGenerated = s._1
      challengesTried = s._2
      log.info(s"Recovered from snapshot: $metadata")
    }
    case s if s == snapMessage =>
      saveSnapshot((langGenerated, challengesTried))
    case SaveSnapshotSuccess(metadata) =>
      try scheduleSnapshot()
      finally log.info(s"Snapshot saved $metadata")
    case SaveSnapshotFailure(metadata, reason) =>
      log.error(s"Snapshot $metadata failed", reason)
    case msg => log.error(s"received unknown message: $msg")
  }

  private def scheduleSnapshot(d: FiniteDuration = SnapshotDuration) = {
    import vipo.guess.Bootstrap._
    system.scheduler.scheduleOnce(d, self, snapMessage)
  }
}