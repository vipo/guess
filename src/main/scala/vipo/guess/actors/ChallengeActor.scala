package vipo.guess.actors

import akka.persistence.Persistent
import vipo.guess.domain.Challenge._
import vipo.guess.domain.Function
import vipo.guess.domain.Language._
import vipo.guess.domain.Language

case class SingleChallengeData(
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
      val size = challengesData.size
      val f: Function = Language.randomFunction(no)._1
      challengesData = challengesData + ((size + 1) ->
      	  SingleChallengeData(no, f, false)) 
    }
    case msg@GenerateChallenge(_) => self forward Persistent(msg)
    //
    case Persistent(MarkAsSolved(challengeId), _) => {
      val old = challengesData(challengeId)
      challengesData = challengesData + (challengeId ->
      	  SingleChallengeData(old.langNo, old.function, true))
    }
    case msg@MarkAsSolved(_) => self forward Persistent(msg)
    //
    case GetChallengesForLanguage(no) => {
      val result: Seq[SingleChallengeData] = challengesData.map(_._2).filter(_.langNo == no).toSeq
      sender ! result
    }
  }

  def dataForSave(): Map[ChallengeId, SingleChallengeData] = challengesData

  def loadData(data: Map[ChallengeId, SingleChallengeData]): Unit = {
    challengesData = data
  }

}