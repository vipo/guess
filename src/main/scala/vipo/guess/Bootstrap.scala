package vipo.guess

import akka.actor.{ActorSystem, Props}
import vipo.guess.actors._

object Bootstrap {

  implicit val System = ActorSystem()

  val Router = System.actorOf(Props[RouterActor], name = "router")

  val Stats = System.actorOf(Props[StatisticsActor], name = "stats")

  val Challenges = System.actorOf(Props[ChallengeActor], name = "challenges")

}