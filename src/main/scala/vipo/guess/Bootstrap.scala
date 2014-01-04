package vipo.guess

import akka.actor.{ActorSystem, Props}
import vipo.guess.actors.RouterActor
import vipo.guess.actors.StatisticsActor

import akka.util.Timeout
import scala.concurrent.duration._

object Bootstrap {
  
  implicit val defaultTimeout = Timeout(2 seconds)
  
  implicit val system = ActorSystem()
  
  implicit val executionContext = system.dispatcher

  val router = system.actorOf(Props[RouterActor], name = "router")
  
  val stats = system.actorOf(Props[StatisticsActor], name = "stats")

}