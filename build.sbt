name := "guess"

version := "0.1"

scalaVersion := "2.10.2"

mainClass := Some("vipo.guess.Main")

resolvers ++= Seq(
  "Typesafe repo" at "http://repo.typesafe.com/typesafe/releases/",
  "Spray repo" at "http://repo.spray.io"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.2.3",
  "com.typesafe.akka" %% "akka-slf4j" % "2.2.3",
  "io.spray" % "spray-can" % "1.2.0",
  "io.spray" % "spray-routing" % "1.2.0",
  "ch.qos.logback" % "logback-classic" % "1.0.13",
  "org.scalatest" %% "scalatest" % "2.0" % "test"
)
