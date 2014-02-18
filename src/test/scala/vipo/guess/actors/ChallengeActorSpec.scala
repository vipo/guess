package vipo.guess.actors

import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.Props
import akka.testkit.TestKit
import org.scalatest.WordSpecLike
import org.scalatest.Matchers
import org.scalatest.BeforeAndAfterAll
import akka.testkit.ImplicitSender

class ChallengeActorSpec extends TestActorSystem {

  val challenges = system.actorOf(Props[ChallengeActor])
  
  "Persistent Challenge actor" must {
    "Return no data for unknown language" in {
      challenges ! GetChallengesForLanguage(100)
      expectMsg(Nil)
    }
    "Return empty data set if there are no challenges for language" in {
      challenges ! GetChallengesForLanguage(1)
      expectMsg(Nil)
    }
    "Do nothing on solving non-existing challege" in {
       challenges ! MarkAsSolved(-1)
       expectNoMsg
    }
    "Return bigger dataset after challenge generated" in {
      challenges ! GetChallengesForLanguage(1)
      expectMsg(Nil)
      challenges ! GetChallengesForLanguage(4)
      expectMsg(Nil)
      challenges ! GenerateChallenge(4)
      expectNoMsg
      challenges ! GenerateChallenge(4)
      expectNoMsg
      challenges ! GetChallengesForLanguage(1)
      expectMsg(Nil)
      challenges ! GetChallengesForLanguage(4)
      val challengeData = expectMsgClass(classOf[List[SingleChallengeData]]) match {
        case List(SingleChallengeData(_, 4, _, false),
            SingleChallengeData(challengeId, 4, function, false)) => (challengeId,  function)
      }
      val ChallengeId = challengeData._1
      val Function = challengeData._2
      challenges ! MarkAsSolved(ChallengeId)
      expectNoMsg
      challenges ! GetChallengesForLanguage(4)
      expectMsgClass(classOf[List[SingleChallengeData]]) match {
        case List(SingleChallengeData(_, 4, _, false),
          SingleChallengeData(ChallengeId, 4, Function, true)) => {}
      }
    }
  }

}