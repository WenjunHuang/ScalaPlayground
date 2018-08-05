addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
name := "macros_test"
version := "1.0"
organization := "wenjun huang"
libraryDependencies ++= {
  Seq(
    "org.scala-lang" % "scala-reflect" % "2.12.3",
    "org.scalatest" %% "scalatest" % "3.0.0" % "test"
  )
}

