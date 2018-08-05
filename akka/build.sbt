name := "akka"

version := "1.0"

organization := "wenjun huang"

libraryDependencies ++= {
  val akkaVersion = "2.5.14"
  val akkaHttpVersion = "10.1.1"
  Seq(
    "com.typesafe.akka" %% "akka-camel" % akkaVersion,
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
    "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-xml" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion,
    "com.typesafe.akka" %% "akka-typed" % "2.5.8",
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
    "com.lightbend.akka" %% "akka-stream-alpakka-amqp" % "0.14",
    "com.lightbend.akka" %% "akka-stream-alpakka-elasticsearch" % "0.14",
    "org.apache.commons" % "commons-math3" % "3.6.1",
    "org.scalatest" %% "scalatest" % "3.0.0"
  )
}

