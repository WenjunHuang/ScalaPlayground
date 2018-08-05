package tutorial.webapp

import org.scalajs.dom.html

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scalatags.JsDom.all._

@JSExportTopLevel("HelloWorld1")
object HelloWorld1 {
  @JSExport("main")
  def main(target: html.Div): Unit = {
    val (animalA, animalB) = ("fox", "dog")
    target.appendChild(
      div(
        h1("Hello World!"),
        p("The quick brown", b(animalA), " jumps over the lazy ", i(animalB), ".")
      ).render
    )
  }
}
