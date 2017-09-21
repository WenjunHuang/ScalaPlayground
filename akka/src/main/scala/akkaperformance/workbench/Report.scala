package akkaperformance.workbench

import java.lang.management.ManagementFactory
import java.text.SimpleDateFormat
import java.util.Date

import akka.actor.ActorSystem
import akka.event.Logging

import scala.collection.immutable.TreeMap

class Report(system: ActorSystem,
             resultRepository: BenchResultRepository,
             compareResultWith: Option[String] = None) {
  private def dolog = system.settings.config.getBoolean("benchmark.logResult")

  val log = Logging(system, "Report")

  val dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
  val legendTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
  val fileTimestampFormat = new SimpleDateFormat("yyyyMMddHHmmss")


  def compareWithHistoricalTpsChart(statistics: Seq[Stats]): Option[String] = {
    if (statistics.isEmpty)
      None
    else {
      val histTimestamps = resultRepository.getWithHistorical(statistics.head.name, statistics.head.load).map(_.timestamp)
      val statsByTimestamp = TreeMap[Long, Seq[Stats]]() ++
        (for (ts <- histTimestamps) yield {
          val seq =
            for (stats <- statistics) yield {
              val withHistorical = resultRepository.getWithHistorical(stats.name, stats.load)
              val cell = withHistorical.find(_.timestamp == ts)
              cell.getOrElse(Stats(stats.name, stats.load, ts))
            }
          (ts, seq)
        })

      val chartTitle = statistics.last.name + " vs. historical, Throughput (TPS)"
      val chartUrl = GoogleChartBuilder.tpsChartUrl(statsByTimestamp, chartTitle, timeLegend)
      Some(chartUrl)
    }
  }

  def timeLegend(stats: Stats): String = {
    val baseline = if (resultRepository.isBaseline(stats)) "*" else ""
    legendTimeFormat.format(new Date(stats.timestamp)) + baseline
  }

  def percentilesAndMeanChart(stats: Stats): String = {
    val chartTitle = stats.name + " Percentiles and Mean (microseconds)"
    val chartUrl = GoogleChartBuilder.percentilesAndMeanChartUrl(resultRepository.get(stats.name), chartTitle, _.load + " clients")
    chartUrl
  }

  def compareWithHistoricalPercentiliesAndMeanChart(stats: Stats): Option[String] = {
    val withHistorical = resultRepository.getWithHistorical(stats.name, stats.load)
    if (withHistorical.size > 1) {
      val chartTitle = stats.name + " vs. historical, " + stats.load + " client" + ", Percentiles and Mean (microseconds)"
      val chartUrl = GoogleChartBuilder.percentilesAndMeanChartUrl(withHistorical, chartTitle, timeLegend)
      Some(chartUrl)
    } else {
      None
    }
  }

  def formatDouble(value: Double) = {
    BigDecimal(value).setScale(2, BigDecimal.RoundingMode.HALF_EVEN)
  }

  def systemInformation: String = {
    val runtime = ManagementFactory.getRuntimeMXBean
    val os = ManagementFactory.getOperatingSystemMXBean
    val threads = ManagementFactory.getThreadMXBean
    val mem = ManagementFactory.getMemoryMXBean
    val heap = mem.getHeapMemoryUsage

    val sb = new StringBuilder

    sb.append("Benchmark properties:\n")
    sb.append(system.settings.config.getConfig("benchmark").root.render)
    sb.append("\n")

    sb.append("Operating system: ").append(os.getName).append(", ").append(os.getArch).append(", ").append(os.getVersion)
    sb.append("\n")
    sb.append("JVM: ").append(runtime.getVmName).append(" ").append(runtime.getVmVendor)
      .append(" ").append(runtime.getVmVersion)
    sb.append("\n")
    sb.append("Processors: ").append(os.getAvailableProcessors)
    sb.append("\n")
    sb.append("Load average: ").append(os.getSystemLoadAverage)
    sb.append("\n")
    sb.append("Thread count: ").append(threads.getThreadCount).append(" (").append(threads.getPeakThreadCount).append(")")
    sb.append("\n")
    sb.append("Heap: ").append(formatDouble(heap.getUsed.toDouble / 1024 / 1024))
      .append(" (").append(formatDouble(heap.getInit.toDouble / 1024 / 1024))
      .append(" - ")
      .append(formatDouble(heap.getMax.toDouble / 1024 / 1024))
      .append(")").append(" MB")
    sb.append("\n")

    import scala.collection.JavaConverters._
    val args = runtime.getInputArguments.asScala.filterNot(_.contains("classpath")).mkString("\n ")
    sb.append("Args:\n ").append(args)
    sb.append("\n")

    sb.append("Akka version: ").append(system.settings.ConfigVersion)
    sb.append("\n")
    sb.append("Akka config:\n")
    sb.append(system.settings.toString)

    sb.toString

  }

  def html(statistics: Seq[Stats]): Unit = {
    val current = statistics.last
    val sb = new StringBuilder

    val title = current.name + " " + dateTimeFormat.format(new Date(current.timestamp))
    sb.append(header(title))
    sb.append(s"<h1>$title</h1>\n")

    sb.append("<pre>\n")
    val resultTable = formatResultsTable(statistics)
    sb.append(resultTable)
    sb.append("\n</pre>\n")

    sb.append(img(latencyAndThroughputChart(current)))

    compareWithHistoricalTpsChart(statistics).foreach { url => sb.append(img(url)) }

    if (current.max > 0L) {
      sb.append(img(percentilesAndMeanChart(current)))

      for (stats <- statistics) {
        compareWithHistoricalPercentiliesAndMeanChart(stats).foreach { url =>
          sb.append(img(url))
        }
      }
    }

    sb.append("<hr/>\n")
    sb.append("<pre>\n")
    sb.append(systemInformation)
    sb.append("\n</pre>\n")

    val timestamp = fileTimestampFormat.format(new Date(current.timestamp))
    val reportName = current.name + "--" + timestamp + ".html"
    resultRepository.saveHtmlReport(sb.toString, reportName)

    if (doLog) {
      log.info(resultTable + "Charts in html report:" + resultRepository.htmlReportUrl(reportName))
    }
  }

  def latencyAndThroughputChart(stats: Stats) = {
    val chartTitle = stats.name + " Latency (microseconds) and Throughput (TPS)"
    val chartUrl = GoogleChartBuilder.latencyAndThroughputChartUrl(resultRepository.get(stats.name), chartTitle)
    chartUrl
  }

  def img(url: String): String = {
    """<img src="%s" border="0" width="%s" height="%s"/>""".format(
      url, GoogleChartBuilder.ChartWidth, GoogleChartBuilder.ChartHeight + "\n"
    )
  }

  def formatResultsTable(statistics: Seq[Stats]) = {
    val name = statistics.head.name

    val spaces = Vector.fill(85)(" ").mkString
    val headerScenarioCol = ("Scenario" + spaces).take(name.length)

    val headerLine = (headerScenarioCol :: "clients" :: "TPS" :: "mean" :: "5% " :: "25% " :: "50% " :: "75% " :: "95% " :: "Durat." :: "N" :: Nil)
      .mkString("\t")
    val headerLine2 = (spaces.take(name.length) :: "       " :: "   " :: "(us)" :: "(us)" :: "(us)" :: "(us)" :: "(us)" :: "(us)" :: "(s)   " :: " " :: Nil)
      .mkString("\t")
    val line = List.fill(formatStats(statistics.head).replaceAll("\t", "      ").length)("-").mkString
    val formattedStats = "\n" +
      line.replace('-', '=') + "\n" +
      headerLine + "\n" +
      headerLine2 + "\n" +
      line + "\n" +
      statistics.map(formatStats(_)).mkString("\n") + "\n" +
      line + "\n"

    formattedStats
  }

  def formatStats(stats: Stats) = {
    val durationS = stats.durationNanos.toDouble / 1000000000.0
    val duration = durationS.formatted("%.0f")

    val tpsStr = stats.tps.formatted("%.0f")
    val meanStr = stats.mean.formatted("%.0f")

    val summaryLine =
      stats.name ::
        stats.load.toString ::
        tpsStr ::
        meanStr ::
        stats.percentiles(5).toString ::
        stats.percentiles(25).toString ::
        stats.percentiles(75).toString ::
        stats.percentiles(95).toString ::
        duration ::
        stats.n.toString ::
        Nil
    summaryLine.mkString("\t")
  }


  def header(title: String) =
    s"""
       |<!DOCTYPE html>
       |<html>
       |<head>
       |<title>$title</title>
       |</head>
       |<body>
    """.stripMargin

  def footer =
    """
      |</body>
      |</html>
    """.stripMargin

  def doLog = system.settings.config.getBoolean("benchmark.logResult")
}
