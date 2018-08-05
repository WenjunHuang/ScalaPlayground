name := "ScalaPlayground"
scalaVersion := "2.12.6"
organization := "wenjun huang"
version := "beta"

lazy val scalaLang = project.in(file("scala_lang"))
lazy val fp = project.in(file("functional"))
lazy val akka = project.in(file("akka"))
lazy val akkaCluster = project.in(file("akka_cluster"))
lazy val scalaFX = project.in(file("scalafx"))
lazy val lwjgl = project.in(file("lwjgl"))
lazy val macros = project.in(file("macros"))
lazy val macrosTest = project.in(file("macros_test")).dependsOn(macros)
lazy val logging = project.in(file("logging"))
lazy val vertx = project.in(file("vertx"))
lazy val springboot = project.in(file("springboot"))
lazy val jmonkey = project.in(file("jmonkey"))
lazy val libgdx = project.in(file("libgdx"))
lazy val jogl = project.in(file("jogl"))
lazy val jni = project.in(file("jni"))
lazy val llvm = project.in(file("llvm"))
lazy val scalajs = project.in(file("scalajs"))
lazy val slick = project.in(file("slick"))
lazy val spark = project.in(file("spark"))
lazy val reflection = project.in(file("reflection"))
lazy val rxjava = project.in(file("rxjava"))
lazy val antlr = project.in(file("antlr"))
lazy val netty = project.in(file("netty_learn"))
lazy val webgl = project.in(file("webgl"))






