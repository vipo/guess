package vipo.guess

import akka.io.IO
import spray.can.Http
import Bootstrap.{System, Router}
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

object Main extends App {

  IO(Http) ! Http.Bind(Router, interface = "localhost", port = 8888)

}