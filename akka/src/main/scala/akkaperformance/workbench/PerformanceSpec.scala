package akkaperformance.workbench

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.Config
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpecLike}

import scala.collection.immutable.TreeMap
import scala.concurrent.duration._

abstract class PerformanceSpec(cfg: Config = BenchmarkConfig.config()) extends TestKit(ActorSystem("PerformanceTest", cfg)) with WordSpecLike with Matchers with BeforeAndAfterEach with ImplicitSender {
  def config = system.settings.config

  def isLongRunningBenchmark = config.getBoolean("benchmark.longRunning")

  def minClients = config.getInt("benchmark.minClients")

  def maxClients = config.getInt("benchmark.maxClients")

  def repeatFactor = config.getInt("benchmark.repeatFactor")

  def timeDilation = config.getLong("benchmark.timeDilation")

  def maxRunDuration = config.getDuration("benchmark.maxRunDuration", TimeUnit.MILLISECONDS) milliseconds

  def clientDelay = config.getDuration("benchmark.clientDelay", TimeUnit.NANOSECONDS) nanoseconds

  val resultRepository = BenchResultRepository()
  lazy val report = new Report(system, resultRepository, compareResultWith)

  /**
    * 如果需要与其他测试结果比较，那么可override这个方法，并返回需要比较的测试名
    *
    * @return
    */
  def compareResultWith: Option[String] = None

  def acceptClients(numberOfClients: Int): Boolean = {
    (minClients <= numberOfClients && numberOfClients <= maxClients) //&& (maxClients <= 16 || numberOfClients % 4 == 0)
  }

  def logMeasurement(numberOfClients: Int, durationNs: Long, n: Long): Unit = {
    val name = self.path.name
    val durationS = (durationNs nanoseconds) toUnit TimeUnit.SECONDS

    val stats = Stats(
      name,
      load = numberOfClients,
      timestamp = System.currentTimeMillis(),
      durationNanos = durationNs,
      n = n,
      tps = (n.toDouble / durationS))

    logMeasurement(stats)
  }

  def logMeasurement(numberOfClients: Int, durationNs: Long, stat: DescriptiveStatistics): Unit = {
    val name = self.path.name
    val durationS = (durationNs nanoseconds) toUnit TimeUnit.SECONDS

    val percentiles = TreeMap[Int, Long](
      5 -> (stat.getPercentile(5.0) / 1000).toLong,
      25 -> (stat.getPercentile(25.0) / 1000).toLong,
      50 -> (stat.getPercentile(50.0) / 1000).toLong,
      75 -> (stat.getPercentile(75.0) / 1000).toLong,
      95 -> (stat.getPercentile(95.0) / 1000).toLong)

    val n = stat.getN
    val stats = Stats(
      name,
      load = numberOfClients,
      timestamp = System.currentTimeMillis(),
      durationNanos = durationNs,
      n = n,
      min = (stat.getMin / 1000).toLong,
      max = (stat.getMax / 1000).toLong,
      mean = (stat.getMean / 1000).toLong,
      tps = (n.toDouble / durationS),
      percentiles)

    logMeasurement(stats)
  }

  def logMeasurement(stats: Stats): Unit = {
    resultRepository.add(stats)
    report.html(resultRepository.get(stats.name))
  }
}
