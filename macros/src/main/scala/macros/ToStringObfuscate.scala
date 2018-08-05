package macros

import scala.annotation.StaticAnnotation
import scala.annotation.compileTimeOnly
import scala.reflect.api.Trees
import scala.reflect.macros.whitebox

@compileTimeOnly("compile time only macro")
class ToStringObfuscate(fieldsToObjfuscate:String*) extends StaticAnnotation {
  def macroTransform(annottees:Any*):Any = macro ToStringObfuscate.impl

}

object ToStringObfuscate {
  def obfuscateValue(value:String) = "*" * value.length

  def impl(c:whitebox.Context)(annottees:c.Expr[Any]*):c.Expr[Any] = {
    import c.universe._


    def extractAnnotationParameters(tree: Tree):Seq[Trees#Tree] = tree match {
      case q"new $name(..$params)" ⇒ params
      case _⇒throw new Exception("TOStringObfuscate annotation must be at least one parameter.")
    }

    def replaceCaseClassSensitiveValues(tree: Trees#Tree) = tree match {
      case Literal(Constant(field:String)) ⇒
        q"""
           ${TermName(field)} = macros.ToStringObfuscate.obfuscateValue(this.${TermName(field)})
         """
      case _⇒c.abort(c.enclosingPosition,s"[obfuscateValue] Match error with $tree")
    }

    val sensitiveFields = extractAnnotationParameters(c.prefix.tree)
    val fieldReplacements = sensitiveFields map (replaceCaseClassSensitiveValues(_))

    def extractNewToString(trees: Seq[Trees#Tree]) =
      q"""
         override def toString:${typeOf[String]} = {
         scala.runtime.ScalaRunTime._toString(this.copy(..$fieldReplacements))
         }
       """

    def modifiedDeclaration(classDecl:ClassDef) = {
      val (className,fields,parents,body) = extractCaseClassesParts(classDecl)
      val newToString = extractNewToString(sensitiveFields)
      val params = fields.asInstanceOf[List[ValDef]] map {p ⇒ p.duplicate}

      c.Expr[Any] {
        q"""
           case class $className(..$params) extends ..$parents {
           $newToString
           ..$body
           }
         """
      }
    }
    def extractCaseClassesParts(classDecl:ClassDef) = classDecl match {
      case q"case class $className(..$fields) extends ..$parents { ..$body}" ⇒
        (className,fields,parents,body)
    }

    annottees map (_.tree) toList match {
      case (classDecl:ClassDef)::Nil ⇒ modifiedDeclaration(classDecl)
      case _⇒c.abort(c.enclosingPosition,"Invalid annottee")
    }
  }

}