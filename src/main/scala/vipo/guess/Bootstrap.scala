package vipo.guess

import akka.actor.{ActorSystem, Props}
import vipo.guess.actors.RouterActor
import vipo.guess.actors.StatisticsActor
import akka.util.Timeout
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConversions._

object Bootstrap {
  
  implicit val DefaultTimeout = Timeout(2 seconds)
    
  implicit val System = ActorSystem()
  
  implicit val ExecutionContext = System.dispatcher

  val SnapshotDuration = 20 hours

  val SnapshotDurationInitial = 4 minutes
  
  val Passwords = System.settings.config.getStringList("passwords")
  
  val Router = System.actorOf(Props[RouterActor], name = "router")
  
  val Stats = System.actorOf(Props[StatisticsActor], name = "stats")

}