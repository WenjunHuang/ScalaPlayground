name := "spark"

version := "1.0"

organization := "wenjun huang"

libraryDependencies ++= {
  Seq(
   "org.apache.spark" %% "spark-core" % "2.2.0",
  "org.scalatest" %% "scalatest" % "3.0.0"
  )
}

