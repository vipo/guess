package vipo.guess.actors

import akka.actor.Actor
import akka.actor.ActorLogging

case class GenerateSample(val langNo: Int)
case class GetSampleGeneratedTimes(val langNo: Int)

class StatisticsActor extends Actor with ActorLogging {

  private var langGenerated: Map[Int, Int] = Map().withDefaultValue(0)
  
  def receive = {
    case GenerateSample(no) => langGenerated + (no -> (langGenerated(no) + 1))
    case GetSampleGeneratedTimes(no) => sender ! langGenerated(no)
    case _ => log.error("received unknown message")
  }
}