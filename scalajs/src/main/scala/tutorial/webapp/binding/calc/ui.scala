package tutorial.webapp.binding.calc

import com.thoughtworks.binding.{Binding, dom}
import com.thoughtworks.binding.Binding.{BindingSeq, Constants, Var}
import org.scalajs.dom.{Event, Node}
import org.scalajs.dom.html.Anchor

class ui {
  val calc = Var(CalcModel())

  def name: String = "playground-binding.scala/calc"

  def description: Binding[Node] = <div>A very simple and basic calculator</div>

  def link: String = s"#playground-binding.scala/calc"

  def scalaFiddle: Option[String] = Some("https://scalafiddle.io/sf/hbwbC0e/0")

  @dom def css: Binding[BindingSeq[Node]] = <style>
    {s"""
         .calc {
            width: 400px;
            border-style: solid;
            border-width: 1px;
            border-color: #e8e8e8;
            border-radius: 10px;
            background-color: #f7f7f7;
            padding: 10px;
         }
         .calc .display {
            border-radius: 7px;
            background-color: #eee;
            margin: 0px 15px 15px 0px;
         }
         .calc .lcd {
            font-family: 'Cutive Mono', monospace;
            text-align:right;
            font-size: 21px;
         }
         .calc .tag {
            font-size: 10px;
         }
         .calc .btn {
            width: 60px;
            padding: 0px;
         }
      """
    }
    </style>

  @dom
  def render: Binding[Node] = {
    val btns = List(List("7", "8", "9", "+", "C"),
                    List("4", "5", "6", "-", "MS"),
                    List("1", "2", "3", "x", "MR"),
                    List(".", "0", "=", "/", "MC"))
    <div class="container">
      <h5>Calc</h5>
      <hr/>
      <p>A very simple calc implementation.</p>
      <div class="calc">
        <div class="row display">
          <div class="col s11">
            <input class="lcd" type="Text" readOnly={true} value={display.bind}></input>
          </div>
          {renderMemoryTag.bind}
          {renderOperatorTag.bind}
        </div>
        {
        Constants(btns: _*).map { l =>
          <div class="row">
            {
              Constants(l: _*).map { c =>
                val push = if (List("+", "-", "x", "/", "C", "MS", "MR", "MC").contains(c)) "push-s1" else ""
                <div class={s"col s2 ${push}"}>{b(c).bind}</div>
              }
            }
            </div>
        }}
        </div>
        </div>
  }

  @dom
  def renderMemoryTag =
  <div class="tag">
    {calc.bind.memory.map(it=>"M").getOrElse(" ")
  </div>

  @dom
  def renderOperatorTag = <div class="tag">{calc.bind.operators.headOption.map {
    case Plus() => "+"
    case Minus() => "-"
    case Multiply() => "x"
    case Divide() => "/"
    case _ => " "
  }.getOrElse(" ")}</div>

  @dom
  def display = Option(calc.bind.accumulator).filterNot(_.isEmpty).getOrElse("0")

  @dom
  def b(label: String): Binding[Anchor] = {
    val op: (String, Token) = label match {
      case "1" | "2" | "3" | "4" | "5" | "6" | "7" | "8" | "9" | "0" ⇒ ("grey darken-1", Digit(label.toInt))
      case "." ⇒ ("grey darken-1", Dot())
      case "+" ⇒ ("blue lighten-5 blue-text text-darken-3", Plus())
      case "-" ⇒ ("blue lighten-5 blue-text text-darken-3", Minus())
      case "x" ⇒ ("blue lighten-5 blue-text text-darken-3", Multiply())
      case "/" ⇒ ("blue lighten-5 blue-text text-darken-3", Divide())
      case "=" ⇒ ("grey lighten-2 black-text text-darken-3", Result())
      case "C" ⇒ ("red lighten-5 red-text text-darken-3", Clear())
      case "MR" ⇒ ("green lighten-5 green-text", MR())
      case "MC" ⇒ ("green lighten-5 green-text", MC())
      case "MS" ⇒ ("green lighten-5 green-text", MS())
      case _ ⇒ ("", NoOp())
    }

    def disabled = Binding {
      if (calc.bind.isDefinedAt(op._2)) "" else "disabled"
    }
    <a class={s"btn ${op._1} ${disabled.bind} waves-effect waves-light"} onclick={_: Event ⇒
      calc.value = calc
        .value(op._2)}>
      {label}
    </a>
    }

}
