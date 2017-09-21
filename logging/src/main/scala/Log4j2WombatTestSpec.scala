import org.apache.logging.log4j.LogManager
import org.scalatest.{Matchers, WordSpec}
import org.slf4j.LoggerFactory

class Log4j2WombatTestSpec extends WordSpec with Matchers{
  "logger with the same name" should {
    "equal" in {
      val x = LogManager.getLogger("wombat")
      val y = LogManager.getLogger("wombat")
      x shouldEqual y
    }
  }

}
