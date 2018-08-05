import com.typesafe.config.ConfigFactory

package object persistence {
  val config = ConfigFactory
    .load("persistence-test")
    .getConfig("persistence")
    .withFallback(ConfigFactory.load())

}
