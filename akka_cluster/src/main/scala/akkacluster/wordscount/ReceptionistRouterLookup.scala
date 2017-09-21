package akkacluster.wordscount

import akka.actor.Actor
import akka.cluster.routing.{ClusterRouterGroup, ClusterRouterGroupSettings}
import akka.routing.BroadcastGroup

trait ReceptionistRouterLookup {
  this: Actor =>
  def receptionistRouter = context.actorOf(
    ClusterRouterGroup(
      BroadcastGroup(Nil),
      ClusterRouterGroupSettings(
        totalInstances = 100,
        routeesPaths = List("/user/receptionist"),
        allowLocalRoutees = true,
        useRole = Some("master")
      )
    ).props(), name = "receptionist-router")

}
