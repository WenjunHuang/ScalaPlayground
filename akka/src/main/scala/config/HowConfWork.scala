package config

import com.typesafe.config.ConfigFactory

object HowConfWork extends App {
  val config = ConfigFactory
    .load("first")
    .withFallback(ConfigFactory.load("second"))

  println(config.getString("foo.bar"))
  println(config.getString("foo.fazz"))

}
