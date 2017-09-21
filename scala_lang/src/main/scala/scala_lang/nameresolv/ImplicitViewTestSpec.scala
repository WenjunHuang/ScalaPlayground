package scala_lang.nameresolv

import org.scalatest.{FunSuite, Matchers}

class FileWrapper(val file: java.io.File) {
  def /(next: String) = new FileWrapper(new java.io.File(file, next))
}

object FileWrapper {
  implicit def wrap(file: java.io.File) = new FileWrapper(file)

  implicit def unwrap(wrapper: FileWrapper) = wrapper.file
}

class ImplicitViewTestSpec extends FunSuite with Matchers {
  test("File should implicitly transfer to wrapper and transfer back") {
    import FileWrapper._

    def userFile(file:java.io.File) =file.getCanonicalFile
    val cur = new java.io.File("/temp")

    userFile(cur / "temp.txt") should be(new java.io.File("/temp/temp.txt"))


  }
}
