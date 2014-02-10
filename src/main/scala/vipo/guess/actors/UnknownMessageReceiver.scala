package vipo.guess.actors

import akka.event.LoggingAdapter
import akka.actor.ActorLogging

trait UnknownMessageReceiver {

  self: ActorLogging =>
  
  def receiveUnknown: PartialFunction[Any, Unit] = {
    case msg => log.error(s"received unknown message: $msg")
  }

}