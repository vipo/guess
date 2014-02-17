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
import vipo.guess.actors.support.UnknownMessageReceiver
import vipo.guess.actors.support.PersistentActor

case class SampleGenerated(val langNo: LangNo)
case class GetSampleGeneratedTimes(val langNo: LangNo)
case class ChallengeQueried(val challengeId: ChallengeId)
case class GetChallengeQueriedTimes(val challengeId: ChallengeId)

object StatisticsActor {
  type SampleGenData = Map[LangNo, Long]
  type ChallengeData = Map[ChallengeId, Long]
}

@SerialVersionUID(42L)
case class SnapshotData(
  val sampleGenData: StatisticsActor.SampleGenData,
  val challengeData: StatisticsActor.ChallengeData) extends Serializable

class StatisticsActor extends PersistentActor[SnapshotData] with UnknownMessageReceiver {
  import StatisticsActor._
  
  private var langGenerated: SampleGenData = Map().withDefaultValue(0)
  private var challengesTried: ChallengeData = Map().withDefaultValue(0)

  override def receive = super.receive orElse doReceive orElse receiveUnknown
  
  private def doReceive: PartialFunction[Any, Unit] = {
    case Persistent(SampleGenerated(no), _) => {
      val curr: Long = langGenerated(no)
      langGenerated = langGenerated + (no -> (curr + 1))
    }
    case msg@SampleGenerated(_) => self forward Persistent(msg)
    case GetSampleGeneratedTimes(no) => sender ! langGenerated(no)
    //
    case Persistent(ChallengeQueried(challengeId), _) => {
      val curr: Long = challengesTried(challengeId)
      challengesTried = challengesTried + (challengeId -> (curr + 1))
    }
    case msg@ChallengeQueried(_) => self forward Persistent(msg)
    case GetChallengeQueriedTimes(challengeId) => sender ! challengesTried(challengeId)
  }
  
  override def loadData(data: SnapshotData): Unit = {
    langGenerated = data.sampleGenData
    challengesTried = data.challengeData
  }

  override def dataForSave: SnapshotData = SnapshotData(langGenerated, challengesTried)

}