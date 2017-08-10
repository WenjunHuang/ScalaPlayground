package reordering

object Main extends App {

  import PossibleReordering._

  val one = new Thread {
    a = 1
    x = b
  }

  val other = new Thread {
    b = 1
    y = a
  }

  one.start()
  other.start()
  one.join()
  other.join()
  println(s"($x,$y)")
}


object PossibleReordering {
  var x = 0
  var y = 0
  var a = 0
  var b = 0
}

