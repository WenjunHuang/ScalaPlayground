package akkasupervision

import java.io.File

object LogProcessing {

}

class DiskError(msg:String) extends java.lang.Error(msg)
class CorruptedFileException(msg:String,val file:File) extends Exception(msg)
class DbBrokenConnectionException(msg:String) extends Exception(msg)
class DbNodeDownException(msg:String) extends Exception(msg)

case class Line(time: Long, message: String, messageType: String)

trait LogParsing {
  def parse(file: File): Vector[Line]
}

trait FileWatchingAbilities {
  def register(uri: String): Unit
}