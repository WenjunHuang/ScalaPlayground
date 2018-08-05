antlr4Settings
name := "antlr"

version := "1.0"

organization := "wenjun huang"
antlr4Version in Antlr4 := "4.7"
libraryDependencies ++= {
  Seq(
    "org.scalatest" %% "scalatest" % "3.0.0",
    "org.antlr" % "antlr4-runtime" % "4.7"
  )
}

