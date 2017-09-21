package akkaperformance.microbench

import java.util.concurrent.{CountDownLatch, TimeUnit}

import akka.actor.{Actor, ActorRef, Props}
import akkaperformance.microbench.TellThroughputComputationPerformanceSpec.{Client, Destination, Run}
import akkaperformance.workbench.{BenchmarkConfig, PerformanceSpec}

class TellThroughputComputationPerformanceSpec extends PerformanceSpec(BenchmarkConfig.config(true)) {

  val repeat = 500L * repeatFactor

  "Tell" should {
    "warmup" in {
      runScenario(4,
                  warmup = true)
    }

    "perform with load 1" in {
      runScenario(1)
    }

    "perform with load 48" in {
      runScenario(48)
    }
  }

  def runScenario(numberOfClients: Int, warmup: Boolean = false): Unit = {

    if (acceptClients(numberOfClients)) {
      val throughputDispatcher = "benchmark.throughput-dispatcher"

      val latch = new CountDownLatch(numberOfClients)
      val repeatsPerClient = repeat / numberOfClients
      val destinations = for (i <- 0 until numberOfClients)
        yield system.actorOf(Props(new Destination).withDispatcher(throughputDispatcher))
      val clients = for (dest <- destinations)
        yield system.actorOf(Props(new Client(dest, latch, repeatsPerClient)).withDispatcher(throughputDispatcher))

      val start = System.nanoTime()
      clients.foreach(_ ! Run)
      val ok = latch.await(maxRunDuration.toMillis, TimeUnit.MILLISECONDS)
      val durationNs = (System.nanoTime() - start)

      if (!warmup) {
        ok should be(true)
        logMeasurement(numberOfClients, durationNs, repeat)
      }
      clients.foreach(system.stop(_))
      destinations.foreach(system.stop(_))
    }
  }
}

object TellThroughputComputationPerformanceSpec {

  case object Run

  case object Msg

  trait PiComputation {
    var pi: Double = 0.0
    var currentPosition = 0L

    def nrOfElements = 1000

    def calculatePi(): Unit = {
      pi += calculateDecimals(currentPosition)
      currentPosition += nrOfElements
    }

    def calculateDecimals(start: Long): Double = {
      var acc = 0.0
      for (i <- start until (start + nrOfElements))
        acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1)
      acc
    }
  }

  class Destination extends Actor with PiComputation {
    override def receive: Receive = {
      case Msg =>
        calculatePi()
        sender ! Msg
    }
  }

  class Client(actor: ActorRef, latch: CountDownLatch, repeat: Long) extends Actor with PiComputation {
    var sent = 0L
    var received = 0L

    override def receive: Receive = {
      case Msg =>
        received += 1
        calculatePi()
        if (sent < repeat) {
          actor ! Msg
          sent += 1
        } else if (received >= repeat) {
          latch.countDown()
        }
      case Run =>
        for (i <- 0L until math.min(1000L, repeat)) {
          actor ! Msg
          sent += 1
        }
    }
  }

}
