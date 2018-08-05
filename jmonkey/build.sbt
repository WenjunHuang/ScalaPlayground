name := "functional"

version := "1.0"

organization := "wenjun huang"
val jmonkeyVersion = "3.1.0-stable"

libraryDependencies ++= Seq(
  "org.jmonkeyengine" % "jme3-core" %jmonkeyVersion,
  "org.jmonkeyengine" % "jme3-desktop" %jmonkeyVersion,
  "org.jmonkeyengine" % "jme3-lwjgl" %jmonkeyVersion,
  "org.scalatest" %% "scalatest" % "3.0.0" % "test"
)

//def jme3 = [v:'3.1.0-stable', g:'org.jmonkeyengine']
//dependencies {
//  compile "${jme3.g}:jme3-core:${jme3.v}"
//  runtime "${jme3.g}:jme3-desktop:${jme3.v}"
//  runtime "${jme3.g}:jme3-lwjgl:${jme3.v}"
//}