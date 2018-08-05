package tutorial.webapp

import org.scalajs.dom
import org.scalajs.dom.html

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scalatags.JsDom.all._

@JSExportTopLevel("SearchBox")
object SearchBox {
  @JSExport
  def main(target:html.Div): Unit = {
    val listings = Seq("Apple","Apricot","Banana","Cherry",
                       "Mango","Mangosteen","Mandarin","Grape",
                       "Grapefruit","Guava")

    val box = input(
      `type` := "text",
      placeholder := "type here"
    ).render


    def renderListings = ul(
      for {
        fruit ← listings if fruit.toLowerCase.startsWith(box.value.toLowerCase)
      } yield {
        val (first,last) = fruit.splitAt(box.value.length)
        li(
          span(
            backgroundColor:="yellow",
            first
          ),
          last
        )
      }
    ).render

    val output = div(renderListings).render

    box.onkeyup = (_:dom.Event) ⇒{
      output.innerHTML = ""
      output.appendChild(renderListings)
    }

    target.appendChild(
      div(
        h1("Search Box!"),
        p("Type here to filter " + "the list of things below!"),
        div(box),
        output
      ).render
    )
  }


}
