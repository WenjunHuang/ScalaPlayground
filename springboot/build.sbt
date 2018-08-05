name := "functional"

version := "1.0"
lazy val springVersion = "1.5.3.RELEASE"
lazy val thymeleafVersion = "2.1.5.RELEASE"
organization := "wenjun huang"

libraryDependencies ++= {
  Seq(
    "org.springframework.boot" % "spring-boot-starter-web" % springVersion,
    "org.springframework.boot" % "spring-boot-starter-data-jpa" % springVersion,
    "org.springframework.boot" % "spring-boot-starter-actuator" % springVersion,
    "org.thymeleaf" % "thymeleaf-spring4" % thymeleafVersion,
    "nz.net.ultraq.thymeleaf" % "thymeleaf-layout-dialect" % "1.4.0",
    "com.h2database" % "h2" % "1.4.195",
    "org.webjars" % "bootstrap" % "3.1.1",
    "org.scalatest" %% "scalatest" % "3.0.0"
  )
}

