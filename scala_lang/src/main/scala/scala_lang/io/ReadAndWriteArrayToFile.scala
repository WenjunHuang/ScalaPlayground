package scala_lang.io

import scala.util.Random

object ReadAndWriteArrayToFile extends App {
  val floats: Array[Double] = new Array(10 ^ 7)
  for (i ← 0 until 10 ^ 7) {
    floats(i) = Random.nextDouble()
  }


}
