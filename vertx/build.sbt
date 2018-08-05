name := "functional"

version := "1.0"

organization := "wenjun huang"
val Version = new {
  final val Scala = "2.12.3"
  final val ScalaTest = "3.0.1"
  final val Vertx = "3.4.2"
}

libraryDependencies ++= Seq(
  "io.vertx" % "vertx-codegen" % Version.Vertx % "provided",
  "io.vertx" %% "vertx-lang-scala" % Version.Vertx,
  "io.vertx" % "vertx-hazelcast" % Version.Vertx,
  "io.vertx" %% "vertx-web-scala" % Version.Vertx,

  "io.vertx" %% "vertx-mqtt-server-scala" % Version.Vertx,
  "io.vertx" %% "vertx-sql-common-scala" % Version.Vertx,
  "io.vertx" %% "vertx-bridge-common-scala" % Version.Vertx,
  "io.vertx" %% "vertx-jdbc-client-scala" % Version.Vertx,
  "io.vertx" %% "vertx-mongo-client-scala" % Version.Vertx,
  "io.vertx" %% "vertx-mongo-service-scala" % Version.Vertx,
  "io.vertx" %% "vertx-auth-common-scala" % Version.Vertx,
  "io.vertx" %% "vertx-auth-shiro-scala" % Version.Vertx,
  "io.vertx" %% "vertx-auth-htdigest-scala" % Version.Vertx,
  "io.vertx" %% "vertx-auth-oauth2-scala" % Version.Vertx,
  "io.vertx" %% "vertx-auth-mongo-scala" % Version.Vertx,
  "io.vertx" %% "vertx-auth-jwt-scala" % Version.Vertx,
  "io.vertx" %% "vertx-auth-jdbc-scala" % Version.Vertx,
  "io.vertx" %% "vertx-web-common-scala" % Version.Vertx,
  "io.vertx" %% "vertx-web-client-scala" % Version.Vertx,
  "io.vertx" %% "vertx-sockjs-service-proxy-scala" % Version.Vertx,
  "io.vertx" %% "vertx-web-templ-freemarker-scala" % Version.Vertx,
  "io.vertx" %% "vertx-web-templ-handlebars-scala" % Version.Vertx,
  "io.vertx" %% "vertx-web-templ-jade-scala" % Version.Vertx,
  "io.vertx" %% "vertx-web-templ-mvel-scala" % Version.Vertx,
  "io.vertx" %% "vertx-web-templ-pebble-scala" % Version.Vertx,
  "io.vertx" %% "vertx-web-templ-thymeleaf-scala" % Version.Vertx,
  "io.vertx" %% "vertx-mysql-postgresql-client-scala" % Version.Vertx,
  "io.vertx" %% "vertx-mail-client-scala" % Version.Vertx,
  "io.vertx" %% "vertx-rabbitmq-client-scala" % Version.Vertx,
  "io.vertx" %% "vertx-redis-client-scala" % Version.Vertx,
  "io.vertx" %% "vertx-stomp-scala" % Version.Vertx,
  "io.vertx" %% "vertx-tcp-eventbus-bridge-scala" % Version.Vertx,
  "io.vertx" %% "vertx-amqp-bridge-scala" % Version.Vertx,
  "io.vertx" %% "vertx-dropwizard-metrics-scala" % Version.Vertx,
  "io.vertx" %% "vertx-hawkular-metrics-scala" % Version.Vertx,
  "io.vertx" %% "vertx-shell-scala" % Version.Vertx,
  "io.vertx" %% "vertx-kafka-client-scala" % Version.Vertx,
  "io.vertx" %% "vertx-circuit-breaker-scala" % Version.Vertx,
  "io.vertx" %% "vertx-config-scala" % Version.Vertx,
  "io.vertx" %% "vertx-service-discovery-scala" % Version.Vertx,
  "io.vertx" %% "vertx-config-git-scala" % Version.Vertx,
  "io.vertx" %% "vertx-config-hocon-scala" % Version.Vertx,
  "io.vertx" %% "vertx-config-kubernetes-configmap-scala" % Version.Vertx,
  "io.vertx" %% "vertx-config-redis-scala" % Version.Vertx,
  "io.vertx" %% "vertx-config-spring-config-server-scala" % Version.Vertx,
  "io.vertx" %% "vertx-config-yaml-scala" % Version.Vertx,
  "io.vertx" %% "vertx-config-zookeeper-scala" % Version.Vertx,

  //non-vert.x deps
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.9.0",
  "org.apache.logging.log4j" % "log4j-api" % "2.9.0",
  "org.apache.logging.log4j" % "log4j-core" % "2.9.0",
  "org.scalatest" %% "scalatest" % Version.ScalaTest,
  "org.hsqldb" % "hsqldb" % "2.4.0"
)

mainClass := Some("io.vertx.core.Launcher")
unmanagedSourceDirectories in Compile := Vector(scalaSource.in(Compile).value)
unmanagedSourceDirectories in Test := Vector(scalaSource.in(Test).value)
initialCommands in console :=
  """|import io.vertx.lang.scala._
     |import io.vertx.lang.scala.ScalaVerticle.nameForVerticle
     |import io.vertx.scala.core._
     |import scala.concurrent.Future
     |import scala.concurrent.Promise
     |import scala.util.Success
     |import scala.util.Failure
     |val vertx = Vertx.vertx
     |implicit val executionContext = io.vertx.lang.scala.VertxExecutionContext(vertx.getOrCreateContext)
     |""".stripMargin