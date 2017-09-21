name := "functional"

version := "1.0"

organization := "wenjun huang"
val catsVersion = "0.9.0"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats" % catsVersion,
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)

