package vipo.guess

import akka.io.IO
import spray.can.Http
import Bootstrap._

object Main extends App {

  IO(Http) ! Http.Bind(router, interface = "localhost", port = 8080)

}