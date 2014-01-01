package vipo.guess

import akka.actor.{ActorSystem, Props}
import vipo.guess.actors.RouterActor
import vipo.guess.actors.StatisticsActor

object Bootstrap {
  
  implicit val system = ActorSystem()

  val router = system.actorOf(Props[RouterActor], name = "router")
  
  val stats = system.actorOf(Props[StatisticsActor], name = "stats")

}