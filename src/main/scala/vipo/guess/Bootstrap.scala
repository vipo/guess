package vipo.guess

import akka.actor.{ActorSystem, Props}
import vipo.guess.actors.RouterActor

object Bootstrap {
  
  implicit val system = ActorSystem()

  val router = system.actorOf(Props[RouterActor], name = "router")
}