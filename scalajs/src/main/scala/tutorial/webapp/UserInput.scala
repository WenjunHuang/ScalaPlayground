package tutorial.webapp

import org.scalajs.dom
import org.scalajs.dom.html

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scalatags.JsDom.all._

@JSExportTopLevel("UserInput")
object UserInput {
  @JSExport("main")
  def main(target:html.Div): Unit = {
    val box = input(
      `type` := "text",
      placeholder := "Type here!"
    ).render

    val output = span.render

    box.onkeyup = (_:dom.Event) ⇒ {
      output.textContent = box.value.toUpperCase()
    }

    target.appendChild(
      div(
        h1("Capital Box!"),
        p(
          "Type here and " + "have it capitalized!"
        ),
        div(box),
        div(output)
      ).render
    )
  }
}
