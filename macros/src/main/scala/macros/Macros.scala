package macros

import scala.collection.immutable.Stack
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros

object Macros {
  def printf(format: String, params: Any*): Unit = macro printf_impl

  def printf_impl(c: Context)(format: c.Expr[String], params: c.Expr[Any]*): c.Expr[Unit] = {
    import c.universe._
    val Literal(Constant(s_format:String)) = format.tree
    val evals = ListBuffer[ValDef]()

    def precompute(value: Tree, tpe: Type): Ident = {
      val freshName = TermName(c.freshName("eval$"))
      evals += ValDef(Modifiers(), freshName, TypeTree(tpe), value)
      Ident(freshName)
    }

    val paramsStack = mutable.Stack[Tree]((params map (_.tree)): _*)
    val refs = s_format.split("(?<=%[\\w%])|(?=%[\\w%])") map {
      case "%d" ⇒ precompute(paramsStack.pop,typeOf[Int])
      case "%s" ⇒ precompute(paramsStack.pop,typeOf[String])
      case "%%" ⇒ Literal(Constant("%"))
      case part ⇒ Literal(Constant(part))
    }

    val stats = evals ++ refs.map(ref ⇒ reify(print(c.Expr[Any](ref).splice)).tree)
    c.Expr[Unit](Block(stats.toList,Literal(Constant())))
  }
}

