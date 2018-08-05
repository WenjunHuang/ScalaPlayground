package myjni

import ch.jodersky.jni.nativeLoader

object HelloJNI extends App {
  System.loadLibrary("jni")

  val h = new HelloJNI
  h.hello()
  println(h.average(1,2))

}

class HelloJNI {
  @native def hello(): Unit

  @native def average(a: Int, b: Int): Double
}
