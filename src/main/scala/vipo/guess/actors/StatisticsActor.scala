package vipo.guess.actors

import akka.actor.Actor
import akka.actor.ActorLogging

import scala.collection.mutable.Map

case class SampleGenerated(val langNo: Int)
case class GetSampleGeneratedTimes(val langNo: Int)

class StatisticsActor extends Actor with ActorLogging {

  private val langGenerated: Map[Int, Long] = Map().withDefaultValue(0)
  
  def receive = {
    case SampleGenerated(no) => langGenerated += (no -> (langGenerated(no) + 1))
    case GetSampleGeneratedTimes(no) => sender ! langGenerated(no)
    case _ => log.error("received unknown message")
  }
}