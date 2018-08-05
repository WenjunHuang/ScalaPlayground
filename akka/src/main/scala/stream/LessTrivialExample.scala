package stream

import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.{ActorMaterializer, KillSwitches}
import akka.stream.scaladsl.{Keep, Sink, Source}

import scala.concurrent.duration._

class PrintMoreNumbers(implicit materializer: ActorMaterializer) extends Actor {
  private implicit val ec = context.system.dispatcher

  val (killSwitch, done) = Source.tick(0 seconds, 1 second, 1)
    .scan(0)(_ + _)
    .map(_.toString)
    .viaMat(KillSwitches.single)(Keep.right)
    .toMat(Sink.foreach(println))(Keep.both)
    .run()

  done.map(_ ⇒ self ! "done")

  override def receive = {
    case "stop"⇒
      println("Stopping")
      killSwitch.shutdown()
    case "done"⇒
      println("Done")
      context.stop(self)
  }
}

object LessTrivialExample extends  App{
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val actorRef = system.actorOf(Props(classOf[PrintMoreNumbers],materializer))
  system.scheduler.scheduleOnce(5 seconds){
    actorRef ! "stop"
  }

}
