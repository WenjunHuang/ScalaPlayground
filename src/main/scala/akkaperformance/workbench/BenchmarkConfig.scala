package akkaperformance.workbench

import com.typesafe.config.ConfigFactory

object BenchmarkConfig {

  val benchmarkConfig = ConfigFactory.load("benchmark")
  val longRunningBenchmarkConfig = ConfigFactory.load("long_running_benchmark").withFallback(benchmarkConfig)

  def config(longRunning: Boolean = false) = if (longRunning)
    longRunningBenchmarkConfig
  else benchmarkConfig

}
