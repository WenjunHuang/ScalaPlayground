name := "netty_learn"

version := "1.0"

organization := "wenjun huang"

libraryDependencies ++= {
  Seq(
    "org.scala-lang" % "scala-reflect" % "2.12.5",
    "io.netty" % "netty-all" % "4.1.24.Final",
    "org.slf4j" % "slf4j-api" % "1.7.25",
    "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.9.0",
    "org.apache.logging.log4j" % "log4j-api" % "2.9.0",
    "org.apache.logging.log4j" % "log4j-core" % "2.9.0",
    "org.scalatest" %% "scalatest" % "3.0.0" % "test"
  )
}

