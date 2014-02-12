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

case class SampleGenerated(val langNo: LangNo)
case class GetSampleGeneratedTimes(val langNo: LangNo)

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
  }
  
  override def loadData(data: SnapshotData): Unit = {
    langGenerated = data.sampleGenData
    challengesTried = data.challengeData
  }

  override def dataForSave: SnapshotData = SnapshotData(langGenerated, challengesTried)

}