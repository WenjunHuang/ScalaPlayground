package tutorial.webapp

import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.jquery.{JQueryAjaxSettings, jQuery}


object TutorialApp {
  def main(): Unit = {
    setupUI()
  }

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    jQuery(targetNode).append(s"<p>$text</p>")
  }

  def setupUI(): Unit = {
    jQuery("""<button type="button">Click me!</button>""")
      .click(addClickedMessage _)
      .appendTo(jQuery("body"))
    jQuery("body").append("<p>Hello World</p>")


    def addClickedMessage(): Unit = {
      appendPar(document.body, "You clicked the button!")
    }

    def rest(): Unit = {
      //    val settings = new JQueryAjaxSettings()
      //    jQuery.ajax("https://api.douban.com/v2/book/search/q=World")
    }
  }
}
