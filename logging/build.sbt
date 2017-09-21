name := "functional"

version := "1.0"

organization := "wenjun huang"
val catsVersion = "0.9.0"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.9.0",
  "org.apache.logging.log4j" % "log4j-api" % "2.9.0",
  "org.apache.logging.log4j" % "log4j-core" % "2.9.0",
  "org.scalatest" %% "scalatest" % "3.0.0"
)

