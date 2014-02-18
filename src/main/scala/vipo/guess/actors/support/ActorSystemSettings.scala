package vipo.guess.actors.support

import akka.actor.Actor
import akka.util.Timeout
import scala.concurrent.duration._
import scala.collection.JavaConversions._

trait ActorSystemSettings {

  self: Actor =>
    
  implicit val DefaultTimeout = Timeout(2 seconds)
  
  implicit val ExecutionContext = context.system.dispatcher
  
  val SnapshotDuration = 20 hours

  val SnapshotDurationInitial = 4 minutes

  lazy val GuessConfig = context.system.settings.config.getConfig("guess")

  lazy val MasterKey: String = GuessConfig.getString("master-key")
  
  lazy val Tokens: Map[Int, String] = GuessConfig.getStringList("tokens").zipWithIndex.
  		map({case (k, i) => (i+1, k)}).toMap

  lazy val SnapshotsDisabled = GuessConfig.getString("snapshots") == "off"

}