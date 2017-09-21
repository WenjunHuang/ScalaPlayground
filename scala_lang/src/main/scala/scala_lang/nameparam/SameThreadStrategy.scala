package scala_lang.nameparam

object SameThreadStrategy  extends ThreadStrategy{
  override def execute[A](func: () ⇒ A) = func
}
