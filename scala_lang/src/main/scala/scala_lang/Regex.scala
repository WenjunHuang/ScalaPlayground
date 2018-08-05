package scala_lang

object Regex extends App {
  val reg = raw"^.+_v(.+)_c(\d+)_(\d{4}\d{2}\d{2}\d{2}\d{2})\.apk$$" r

  val str = "board_v1.0.0_c121_201802031020.apk"

  str match {
    case reg(version,code,date) ⇒
      println(s"$version,$code,$date")
    case _⇒
      println("not match")
  }

}
