import com.typesafe.sbt.SbtMultiJvm.multiJvmSettings
import com.typesafe.sbt.SbtMultiJvm.MultiJvmKeys.MultiJvm

val catsVersion = "0.9.0"
val akkaFullVersion = "2.5.3"
val akkaHttpFullVersion = "10.0.9"
val catsAll = "org.typelevel" %% "cats" % catsVersion
val macroParadise = compilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
val kindProjector = compilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4")
val resetAllAttrs = "org.scalamacros" %% "resetallattrs" % "1.0.0"

lazy val commonSettings = Seq(
  organization := "ScalaPlayground",
  name := "ScalaPlayground",
  scalaVersion := "2.12.2",
  libraryDependencies ++= Seq(
    catsAll,
    macroParadise, kindProjector, resetAllAttrs
  ),
  // https://mvnrepository.com/artifact/org.scalatest/scalatest_2.12
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.3" % "test",
  libraryDependencies += "com.github.mpilquist" %% "simulacrum" % "0.11.0",
  libraryDependencies += "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
  libraryDependencies += "com.softwaremill.macwire" %% "macrosakka" % "2.3.0" % "provided",
  libraryDependencies += "com.softwaremill.macwire" %% "util" % "2.3.0",
  libraryDependencies += "com.softwaremill.macwire" %% "proxy" % "2.3.0",
  libraryDependencies += "com.github.dwickern" %% "scala-nameof" % "1.0.3" % "provided",
  libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaFullVersion,
  libraryDependencies += "com.typesafe.akka" %% "akka-remote" % akkaFullVersion,
  libraryDependencies += "com.typesafe.akka" %% "akka-cluster" % akkaFullVersion,
  libraryDependencies += "com.typesafe.akka" %% "akka-cluster-metrics" % akkaFullVersion,
  libraryDependencies += "com.typesafe.akka" %% "akka-cluster-tools" % akkaFullVersion,
  libraryDependencies += "com.typesafe.akka" %% "akka-http" % akkaHttpFullVersion,
  libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpFullVersion,
  libraryDependencies += "com.typesafe.akka" %% "akka-persistence" % akkaFullVersion,
  libraryDependencies += "com.typesafe.akka" %% "akka-persistence-query" % akkaFullVersion,
  libraryDependencies += "org.iq80.leveldb" % "leveldb" % "0.7",
  libraryDependencies += "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-cluster" % akkaFullVersion,
    "com.typesafe.akka" %% "akka-cluster-sharding" % akkaFullVersion,
    "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaFullVersion % "test",
    "com.typesafe.akka" %% "akka-testkit" % akkaFullVersion % "test"),
  scalacOptions in Compile ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-language:experimental.macros",
    "-language:implicitConversions",
    "-feature",
    "-language:_"
  ),
  fork in run := true,
  mainClass in(Compile, run) := Some("akkaclustersharding.ShardingApp")
)

lazy val macros = project.
  settings(commonSettings: _*).
  settings(name := "Macros")

lazy val root = (project in file("."))
  .settings(multiJvmSettings: _*)
  .settings(commonSettings: _*)
  .aggregate(macros)
  .dependsOn(macros)
  .configs(MultiJvm)





