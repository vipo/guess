package vipo.guess.actors

import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.Props
import akka.testkit.TestKit
import org.scalatest.WordSpecLike
import org.scalatest.Matchers
import org.scalatest.BeforeAndAfterAll
import akka.testkit.ImplicitSender

class StatisticsActorSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
    with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("Spec", TestConfig.Guess))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val stats = system.actorOf(Props[StatisticsActor])
  
  "Persistent Statistics actor" must {
    "Return zero for unknown language sample generation" in {
      stats ! GetSampleGeneratedTimes(100)
      expectMsg(0L)
    }
    "Return increased value after sample generation requested" in {
      stats ! GetSampleGeneratedTimes(1)
      expectMsg(0)
      stats ! SampleGenerated(1)
      expectNoMsg()
      stats ! GetSampleGeneratedTimes(1)
      expectMsg(1)
    }
    "Return zero for unknown language challenge requests" in {
      stats ! GetChallengeQueriedTimes(100)
      expectMsg(0L)
    }
    "Return increased value after challenge queried" in {
      stats ! GetChallengeQueriedTimes(1)
      expectMsg(0)
      stats ! ChallengeQueried(1)
      expectNoMsg()
      stats ! GetChallengeQueriedTimes(1)
      expectMsg(1)
    }
  }
}
