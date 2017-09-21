package scala_lang

package object nameparam {
   val i = ComplexNumber(0.0,1.0)
  implicit def realToComplex(r:Double) = ComplexNumber(r, 0.0)
}
