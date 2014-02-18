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
  
}