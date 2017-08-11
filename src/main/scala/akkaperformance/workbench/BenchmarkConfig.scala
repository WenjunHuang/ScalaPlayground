package akkaperformance.workbench

import com.typesafe.config.ConfigFactory

object BenchmarkConfig {

  val benchmarkConfig = ConfigFactory.load("benchmark")
  val longRunningBenchmarkConfig = ConfigFactory.load("long_running_benchmark").withFallback(benchmarkConfig)
  val throughputDispatcher = "benchmark.throughput-dispatcher"

  def config(longRunning: Boolean = false) = if (longRunning)
    longRunningBenchmarkConfig
  else benchmarkConfig

}
