package vipo.guess

import akka.actor.{ActorSystem, Props}
import vipo.guess.domain.Language.LangNo
import vipo.guess.actors.RouterActor
import vipo.guess.actors.StatisticsActor
import akka.util.Timeout
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import scala.collection.JavaConversions._
import vipo.guess.actors.ChallengeActor

object Bootstrap {

  implicit val DefaultTimeout = Timeout(2 seconds)

  implicit val System = ActorSystem()

  implicit val ExecutionContext = System.dispatcher

  val SnapshotDuration = 20 hours

  val SnapshotDurationInitial = 4 minutes

  val GuessConfig = System.settings.config.getConfig("guess")

  val MasterKey: String = GuessConfig.getString("master-key")
  
  val Tokens: Map[LangNo, String] = GuessConfig.getStringList("tokens").zipWithIndex.
  		map({case (k, i) => (i+1, k)}).toMap

  val Router = System.actorOf(Props[RouterActor], name = "router")

  val Stats = System.actorOf(Props[StatisticsActor], name = "stats")

  val Challenges = System.actorOf(Props[ChallengeActor], name = "challenges")

}