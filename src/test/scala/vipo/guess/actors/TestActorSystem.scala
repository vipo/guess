package vipo.guess.actors

import akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.Props
import akka.testkit.TestKit
import org.scalatest.WordSpecLike
import org.scalatest.Matchers
import org.scalatest.BeforeAndAfterAll
import akka.testkit.ImplicitSender

abstract class TestActorSystem(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
    with WordSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("Spec", TestConfig.Guess))

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

}