package typeclass

trait Codec[A] {
  def encode(value:A):String
  def decode(value:String):Option[A]
  def imap[B](dec:A⇒B,enc:B⇒A):Codec[B] = ???

}
