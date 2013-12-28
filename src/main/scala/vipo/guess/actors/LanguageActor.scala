package vipo.guess.actors

import akka.actor.Actor
import akka.actor.ActorLogging

class LangActor extends Actor with ActorLogging {

  def receive = {
    case "test" => log.info("received test")
    case _      => log.info("received unknown message")
  }
}