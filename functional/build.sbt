name := "functional"

version := "1.0"

organization := "wenjun huang"
val catsVersion = "1.0.1"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)

scalacOptions ++= Seq(
  "-Xfatal-warnings",
  "-Ypartial-unification"
)
