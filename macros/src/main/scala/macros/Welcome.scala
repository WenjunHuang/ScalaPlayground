package macros

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object Welcome {
  def isEvenLog(number:Int):Unit = macro isEvenLogImpl

  def isEvenLogImpl(c:blackbox.Context)(number: c.Tree): c.Tree = {
    import c.universe._

    val result = q"""
        val evaluatedNumber = $number
       if (evaluatedNumber % 2 == 0) {
       println(evaluatedNumber.toString + " is even")
       }else {
       println(evaluatedNumber.toString + " is odd")
       }
      """

    println(showCode(result))
    result
  }
}
