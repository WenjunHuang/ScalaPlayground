import org.slf4j.LoggerFactory

object HelloWorld extends App {

  val logger = LoggerFactory.getLogger(HelloWorld.getClass)
  logger.info("Hello World")

}
