import macros.ToStringObfuscate

@ToStringObfuscate("password")
case class Account(name: String, password: String)

object ToStringObfuscateTest extends App {
  val account = Account("wenjun", "123456")

  println(account)

}
