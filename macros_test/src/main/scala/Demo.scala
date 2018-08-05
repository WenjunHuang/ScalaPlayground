object Demo extends App{

  trait Timeout {}

  case class CreateParam() {}

  case class UpdateParam() {}

  type TimerTask = Function[Timeout, Unit]

  implicit def createTimeTask(param: CreateParam): Unit = {
    println("I am create time task")
  }

  implicit def updateTimeTask(param: UpdateParam):Unit = {
    println("I am update time task")
  }


  class Timer {
    def newTimeout[T](delay: Int, param: T)(implicit f: Function[T, Unit]): Unit = {
      println(f(param))
    }
  }

  val timer                      = new Timer()
  timer.newTimeout(1000, CreateParam())
  timer.newTimeout(2000, UpdateParam())


}
