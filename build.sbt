name := "guess"

version := "0.1"

scalaVersion := "2.10.2"

val akkaVersion = "2.3.0"

val sprayVersion = "1.3.0"

mainClass := Some("vipo.guess.Main")

fork := true

fork in run := true

javaOptions in run += "-Dconfig.file=./guess.conf"

resolvers ++= Seq(
  "Typesafe repo" at "http://repo.typesafe.com/typesafe/releases/",
  "Spray repo" at "http://repo.spray.io"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-experimental" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "io.spray" % "spray-can" % sprayVersion,
  "io.spray" % "spray-routing" % sprayVersion,
  "ch.qos.logback" % "logback-classic" % "1.0.13",
  "org.scalatest" %% "scalatest" % "2.0" % "test",
  "org.scalacheck" %% "scalacheck" % "1.11.3" % "test"
)
