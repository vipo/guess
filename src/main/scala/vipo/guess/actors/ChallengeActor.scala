package vipo.guess.actors

import akka.persistence.Persistent
import vipo.guess.domain.Challenge._
import vipo.guess.domain.Function
import vipo.guess.domain.Language._
import vipo.guess.domain.Language
import vipo.guess.actors.support.UnknownMessageReceiver
import vipo.guess.actors.support.PersistentActor

case class SingleChallengeData(
    challengeId: ChallengeId,
    langNo: LangNo,
    function: Function,
    solved: Boolean
  )

case class GenerateChallenge(val langNo: LangNo)
case class MarkAsSolved(val challengeId: ChallengeId)
case class GetChallengesForLanguage(val langNo: LangNo)

class ChallengeActor extends PersistentActor[Map[ChallengeId, SingleChallengeData]] with UnknownMessageReceiver {

  var challengesData: Map[ChallengeId, SingleChallengeData] = Map()
  
  override def receive = super.receive orElse doReceive orElse receiveUnknown
  
  private def doReceive: PartialFunction[Any, Unit] = {
    case Persistent(GenerateChallenge(no), _) => {
      val f: Function = Language.randomFunction(no)
      val newId = challengesData.size + 1
      challengesData = challengesData + (newId -> SingleChallengeData(newId, no, f, false))
      log.info("Challenge {} generated for lang {}: {}", newId, no, f)
    }
    case msg@GenerateChallenge(_) => self forward Persistent(msg)
    //
    case Persistent(MarkAsSolved(challengeId), _) => {
      challengesData.get(challengeId) match {
        case None => log.info("Trying to mark non-existent challengeId {}", challengeId)
        case Some(old) => challengesData = challengesData + (challengeId -> old.copy(solved = true))
      }
    }
    case msg@MarkAsSolved(_) => self forward Persistent(msg)
    //
    case GetChallengesForLanguage(no) => {
      val result: Seq[SingleChallengeData] = challengesData.map(_._2).
        filter(_.langNo == no).toList.sortWith(_.challengeId < _.challengeId)
      sender ! result
    }
  }

  def dataForSave(): Map[ChallengeId, SingleChallengeData] = challengesData

  def loadData(data: Map[ChallengeId, SingleChallengeData]): Unit = {
    challengesData = data
  }

}