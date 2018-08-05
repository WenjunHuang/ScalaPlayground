package macros

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.whitebox.Context

@compileTimeOnly("use macro paradise")
class Extends[T](defaults: Any*) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro Extends.impl
}

object Extends {
  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    val targetTrait = c.prefix.tree match {
      case q"new $name[$typ]" ⇒ typ
    }

    val tpe = c.typecheck(q"0.asInstanceOf[$targetTrait]").tpe
    val values = tpe.members.collect {
      case d if d.isTerm ⇒ d.asTerm
    }.filter { t ⇒
      t.isStable && t.isMethod && t.isAbstract
    }.toList

    def extractClassNameAndFields(classDecl: ClassDef) = {
      try {
        val q"case class $className(..$fields) extends ..$bases {..$body}" = classDecl
        (className, fields, bases, body)
      } catch {
        case _: MatchError ⇒ c.abort(c.enclosingPosition, "Annotation is only supported on case classes")
      }
    }

    def modifiedFields(fields: Seq[ValDef]): List[ValDef] = {
      val newParams = values.map { v ⇒
        ValDef(Modifiers(Flag.CASEACCESSOR), v.name, TypeTree(v.typeSignature.typeConstructor), EmptyTree)
      }
      newParams ++ fields
    }

    def modifiedClass(classDecl: ClassDef): Expr[_] = {
      val (className, fields, bases, body) = extractClassNameAndFields(classDecl)
      val ctor = modifiedFields(fields.asInstanceOf[Seq[ValDef]])
      c.Expr(q"case class $className(..$ctor) extends  $targetTrait with ..$bases {..$body}")
    }

    annottees.map(_.tree) match {
      case (classDecl: ClassDef) :: Nil ⇒ modifiedClass(classDecl)
      case _ ⇒ c.abort(c.enclosingPosition, "Invalid annottee")
    }
  }
}
