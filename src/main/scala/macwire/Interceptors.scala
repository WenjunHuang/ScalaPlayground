package macwire

import com.softwaremill.macwire._
import com.softwaremill.tagging._

class Berry(){
  def foo(): Unit ={
  }
}

trait Black
trait Blue
case class Basket(blueberry:Berry @@ Blue, blackberry: Berry @@ Black)

object Interceptors extends App {
  lazy val blueberry = wire[Berry].taggedWith[Blue]
  lazy val blackberry = wire[Berry].taggedWith[Black]
  lazy val basket = wire[Basket]
}
