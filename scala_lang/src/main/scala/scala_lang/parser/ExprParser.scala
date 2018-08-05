package scala_lang.parser

import scala.util.parsing.combinator.RegexParsers

sealed trait Expr
case class Number(value:Int) extends Expr
case class Operator(op:String,left:Expr,right:Expr) extends Expr

class ExprParser extends RegexParsers {
  val number = "[0-9]+".r

  def expr: Parser[Int] = term ~ ((("+" | "-") ~ expr)?) ^^ {
    case t ~ None ⇒ t
    case t ~ Some("+" ~ e)⇒t + e
    case t ~ Some("-" ~ e)⇒t - e
  }

  def term: Parser[Int] = factor ~ (("*" ~> factor)*) ^^ {
    case f ~ r⇒f * r.product
  }

  def factor: Parser[Int] = number ^^ { _.toInt } | "(" ~> expr <~ ")" ^^ {
    case e ⇒ e
  }
}
