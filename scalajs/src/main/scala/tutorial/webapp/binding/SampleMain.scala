package tutorial.webapp.binding

import com.thoughtworks.binding.Binding.{BindingSeq, Var, Vars}
import com.thoughtworks.binding.{Binding, dom}

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import org.scalajs.dom.document
import org.scalajs.dom.html.{Button, Table, TableRow}
import org.scalajs.dom.raw.Event

@JSExportTopLevel(name = "BindingSample")
object SampleMain {

  case class Contact(name: Var[String], email: Var[String])

  val data = Vars.empty[Contact]

  @dom
  def bindingButton(contact: Contact): Binding[Button] = {
      <button
        onclick ={ event: Event =>
          contact.name := "Modified Name"
          }
      >Modify the name</button>
  }

  @dom
  def bindingTr(contact: Contact): Binding[TableRow] = {
    <tr>
      <td>{contact.name.bind}</td>
      <td>{contact.email.bind}</td>
      <td>{bindingButton(contact).bind}</td>
    </tr>
  }

  @dom
  def bindingTable(contacts: BindingSeq[Contact]): Binding[Table] = {
    <table>
      <tbody>
        {
        for (contact <- contacts) yield {
          bindingTr(contact).bind
        }
        }
      </tbody>
    </table>

  }

  @JSExport
  def main(): Unit = {
    val data = Vars(Contact(Var("Yang bo"),Var("yang.bo@rea-group.com")))
    dom.render(document.body, bindingTable(data))
  }

}
