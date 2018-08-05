package akkatyped

import akka.typed.{ActorRef, Behavior}
import akka.typed.scaladsl.Actor

object ImmutableRoundRobin {
  def roundRobinBehavior[T](numberOfWorkers: Int, worker: Behavior[T]): Behavior[T] =
    Actor.deferred { ctx ⇒
      val workers = (1 to numberOfWorkers).map { n ⇒
        ctx.spawn(worker, s"worker-$n")
      }
      activeRoutingBehavior(index = 0, workers.toVector)
    }

  private def activeRoutingBehavior[T](index: Int, workers: Vector[ActorRef[T]]): Behavior[T] =
    Actor.immutable[T] { (ctx, msg) ⇒
      workers((index % workers.size).toInt) ! msg
      activeRoutingBehavior(index + 1, workers)
    }

}
