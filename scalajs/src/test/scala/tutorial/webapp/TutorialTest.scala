package tutorial.webapp

import utest._
import org.scalajs.jquery.jQuery

object TutorialTest extends TestSuite{
  TutorialApp.setupUI()
  override def tests = TestSuite {
    'HelloWorld {
      assert(jQuery("p:contains('Hello World')").length == 1)
    }

    'ButtonClick {
      def messageCount = jQuery("p:contains('You clicked the button!')").length
      val button = jQuery("button:contains('Click me!')")
      assert(button.length == 1)
      assert(messageCount == 0)

      for (c ← 1 to 5) {
        button.click()
        assert(messageCount == c)
      }
    }
  }
}
