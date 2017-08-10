package macro_learning

import macros.{Benchmark, TalkingAnimalSpell, Welcome}

trait Animal {
  val name:String
}

@TalkingAnimalSpell
case class Dog(name:String) extends Animal {
  def otherMethod(): Unit ={
    println("other method" + name)
  }
}

object Main extends App {
  val x = 2
  val y = 3
  Welcome.isEvenLog(x + y)

  @Benchmark
  def timlyCaculation(number: Long): Seq[Long] = {
    (2l to number).withFilter { i =>
      (2l to math.sqrt(i).toLong).dropWhile(i % _ != 0) == Nil
    }.map { it => it }
      .toList
  }

  println(timlyCaculation(10000))

//  Dog("spoofer").sayHello
  Dog("spoofer").otherMethod()
}
