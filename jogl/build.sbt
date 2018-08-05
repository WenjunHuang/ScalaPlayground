name := "jogl"

version := "1.0"

organization := "wenjun huang"

libraryDependencies ++= Seq(
  "org.jogamp.jogl" % "jogl-all-main" % "2.3.2",
  "org.jogamp.gluegen" % "gluegen-rt-main" % "2.3.2",
//  "org.jogamp.jogl" % "jogl-all" % "2.3.2" classifier "natives-macosx-universal",
//  "org.jogamp.gluegen" % "gluegen-rt" % "2.3.2" classifier "natives-macosx-universal",

  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)

