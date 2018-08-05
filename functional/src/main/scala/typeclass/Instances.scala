package typeclass

final case class Person(name: String, email: String)

object JsonWriterInstances {
  implicit val stringJsonWriter = new JsonWriter[String] {
    override def write(value: String): Json =
      JsString(value)
  }

  implicit val personJsonWriter = new JsonWriter[Person] {
    override def write(value: Person): Json =
      JsObject(Map("name" → JsString(value.name), "email" → JsString(value.email)))
  }
}
