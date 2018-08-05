import scala.reflect.macros.Context
import scala.language.experimental.macros

case class Location(filename:String,line:Int,column:Int)

object Macros {
  def currentLocation: Location = macro impl

  def impl(c:Context):c.Expr[Location] = {
    import c.universe._
    val pos = c.macroApplication.pos
    val clsLocation = c.mirror.staticModule("Location")
    c.Expr(Apply(Ident(clsLocation),List(Literal(Constant(pos.source.path)),Literal(Constant(pos.line)),Literal(Constant(pos.column)))))
  }
}
