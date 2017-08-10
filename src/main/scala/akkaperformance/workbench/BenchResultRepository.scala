package akkaperformance.workbench

import java.io.File

import scala.collection.mutable

trait BenchResultRepository {
  def add(stats: Stats)

  def get(name: String): Seq[Stats]

  def get(name: String, load: Int): Option[Stats]

  def getWithHistorical(name: String, load: Int): Seq[Stats]

  def isBaseline(stats: Stats): Boolean

  def saveHtmlReport(content: String, name: String): Unit

  def htmlReportUrl(name: String): String
}

object BenchResultRepository {
  val repository = new FileBenchResultRepository

  def apply(): BenchResultRepository = repository
}

class FileBenchResultRepository extends BenchResultRepository {

  case class Key(name: String, load: Int)

  val statsByName = mutable.Map[String, Seq[Stats]]()
  val baselineStats = mutable.Map[Key, Stats]()
  val historicalStats = mutable.Map[Key, Seq[Stats]]()
  val resultDir = BenchmarkConfig.config.getString("benchmark.resultDir")
  val serDir = resultDir + "/ser"

  def serDirExists = new File(serDir).exists

  val htmlDir = resultDir + "/html"

  def htmlDirExists: Boolean = new File(htmlDir).exists()

  val maxHistorical = 7

  override def add(stats: Stats): Unit = synchronized {
    val values = statsByName.getOrElseUpdate(stats.name, IndexedSeq.empty)
    statsByName(stats.name) = values :+ stats
    save(stats)
  }

  override def get(name: String): Seq[Stats] = synchronized {
    statsByName.getOrElse(name, IndexedSeq.empty)
  }

  override def get(name: String, load: Int): Option[Stats] = synchronized {
    get(name).find(_.load == load)
  }

  override def getWithHistorical(name: String, load: Int): Seq[Stats] = synchronized {
    val key = Key(name, load)
    val historical = historicalStats.getOrElse(key, IndexedSeq.empty)
    val baseline = baselineStats.get(key)
    val current = get(name, load)

    val limited = (IndexedSeq.empty ++ historical ++ baseline ++ current).takeRight(maxHistorical)
    limited.sortBy(_.timestamp)
  }

  override def isBaseline(stats: Stats): Boolean = ???

  override def saveHtmlReport(content: String, name: String): Unit = ???

  override def htmlReportUrl(name: String): String = ???
}
