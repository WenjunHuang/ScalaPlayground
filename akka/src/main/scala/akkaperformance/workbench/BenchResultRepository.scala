package akkaperformance.workbench

import java.io._
import java.text.SimpleDateFormat
import java.util.Date

import scala.collection.mutable
import scala.util.{Failure, Try}

trait BenchResultRepository {
  def add(stats: Stats): Try[Unit]

  def get(name: String): Seq[Stats]

  def get(name: String, load: Int): Option[Stats]

  def getWithHistorical(name: String, load: Int): Seq[Stats]

  def isBaseline(stats: Stats): Boolean

  def saveHtmlReport(content: String, name: String): Try[Unit]

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
  val resultDir = BenchmarkConfig.config().getString("benchmark.resultDir")
  val serDir = resultDir + "/ser"

  def serDirExists = new File(serDir).exists

  val htmlDir = resultDir + "/html"

  def htmlDirExists: Boolean = new File(htmlDir).exists()

  val maxHistorical = 7

  loadFiles()

  override def add(stats: Stats): Try[Unit] = synchronized {
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

  override def isBaseline(stats: Stats): Boolean = synchronized {
    baselineStats.get(Key(stats.name, stats.load)) == Some(stats)
  }

  override def saveHtmlReport(content: String, fileName: String): Try[Unit] = {
    new File(htmlDir).mkdirs()
    if (htmlDirExists) {
      val f = new File(htmlDir, fileName)
      var writer: Option[PrintWriter] = None
      val result = Try {
        writer = Some(new PrintWriter(new FileWriter(f)))
        writer.get.print(content)
        writer.get.flush()
      }.recoverWith {
        case e =>
          val errMsg = s"Failed to save report to [${f.getAbsolutePath}], due to [${e.getMessage}]"
          Failure(new Exception(errMsg))
      }
      Try {
        writer.foreach {
          _.close()
        }
      }

      result
    }
    else
      Failure(new FileNotFoundException(htmlDir))
  }

  override def htmlReportUrl(fileName: String): String = new File(htmlDir, fileName).getAbsolutePath

  def loadFiles(): Unit = {
    if (serDirExists) {
      val files =
        for {
          f <- new File(serDir).listFiles
          if f.isFile && f.getName.endsWith(".ser")
        } yield f

      val (baselineFiles, historicalFiles) = files.partition(_.getName.startsWith("baseline-"))
      val baselines = load(baselineFiles)
      for (stats <- baselines) {
        baselineStats(Key(stats.name, stats.load)) = stats
      }
      val historical = load(historicalFiles)
      for (h <- historical) {
        val values = historicalStats.getOrElseUpdate(Key(h.name, h.load), IndexedSeq.empty)
        historicalStats(Key(h.name, h.load)) = values :+ h
      }
    }
  }

  def load(files: Iterable[File]): Seq[Stats] = {
    val result =
      for (f <- files) yield {
        var in: Option[ObjectInputStream] = None
        val result = Try {
          in = Some(new ObjectInputStream(new BufferedInputStream(new FileInputStream(f))))
          in.get.readObject.asInstanceOf[Stats]
        }.toOption
        Try {
          in.foreach { in => in.close() }
        }
        result
      }

    result.flatten.toSeq.sortBy(_.timestamp)
  }

  def save(stats: Stats): Try[Unit] = {
    new File(serDir).mkdirs()
    if (!serDirExists) Failure[Unit](new FileNotFoundException(serDir))

    val timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(stats.timestamp))
    val name = s"${stats.name}--${timestamp}--${stats.load}.ser"
    val f = new File(serDir, name)
    var out: Option[ObjectOutputStream] = None

    val result = Try {
      out = Some(new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f))))
      out.get.writeObject(stats)
    }.recoverWith {
      case e =>
        Failure[Unit](new Exception(s"Failed to save [${stats}] to [${f.getAbsolutePath}], due to [${e.getMessage}]"))
    }

    Try {
      out.foreach(_.close)
    }

    result
  }
}
