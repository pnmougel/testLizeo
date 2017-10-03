import Dependencies._
import sbt.Keys._
import sbt._

lazy val commonSettings = Seq(
  name := "testLizeoPnmougel",
  version := "0.1.0",
  scalaVersion := "2.12.3",
  test in assembly := {}
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(libraryDependencies ++= appDeps)
  .settings(libraryDependencies ++= testDeps)
  .settings(Revolver.settings)
  .settings(
    mainClass in assembly := Some("org.lizeo.backend.core.Boot"),
    assemblyJarName in assembly := "app.jar"
  )

mainClass in(Compile, packageBin) := Some("org.lizeo.backend.core.Boot")

mainClass in Revolver.reStart := Some("org.lizeo.backend.core.Boot")

// Merge strategy used for the assembly task
assemblyMergeStrategy in assembly := {
  case x if x.endsWith("io.netty.versions.properties") => MergeStrategy.first
  case x => {
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
  }
}

addCommandAlias("run", "~re-start")
