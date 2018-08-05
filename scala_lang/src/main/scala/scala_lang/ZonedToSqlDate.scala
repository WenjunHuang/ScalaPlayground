package scala_lang

import java.sql.Date
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object ZonedToSqlDate extends App {
  val zoned = ZonedDateTime.parse("20180306T171048+0800", DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssZ"))
  println(zoned)

  println(zoned.toLocalDate)
  println(Date.valueOf(zoned.toLocalDate))

}
