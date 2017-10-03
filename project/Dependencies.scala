import sbt._

object Dependencies {
  // Versions
  lazy val akkaVersion = "2.4.19"
  lazy val akkaHttpVersion = "10.0.10"
  lazy val scalaCsvVersion = "1.3.5"
  lazy val log4jVersion = "2.9.0"

  val appDeps = Seq(
    // Akka core
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,

    // Akka http
    "com.typesafe.akka" % "akka-http_2.12" % akkaHttpVersion,

    // CSV reader
    "com.github.tototoshi" %% "scala-csv" % scalaCsvVersion,

    // Logs
    "org.apache.logging.log4j" % "log4j-core" % log4jVersion,
    "org.apache.logging.log4j" % "log4j-api" % log4jVersion,
    "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4jVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,

    // Json
    "com.typesafe.akka" % "akka-http-jackson_2.12" % akkaHttpVersion,
    "com.fasterxml.jackson.module" % "jackson-module-scala_2.12" % "2.8.4" force()
  )

  val testDeps = Seq(
    // Akka testing framework
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,

    // Scala test framework
    "org.scalatest" %% "scalatest" % "3.0.1" % Test
  )
}
