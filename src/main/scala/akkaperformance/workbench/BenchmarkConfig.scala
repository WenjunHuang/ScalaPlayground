package akkaperformance.workbench

import com.typesafe.config.ConfigFactory

object BenchmarkConfig {

  val config = ConfigFactory.load("benchmark")

}
