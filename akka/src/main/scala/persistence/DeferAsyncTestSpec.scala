package persistence

import akka.actor.{ActorSystem, Props}
import akka.persistence.{PersistentActor, Recovery}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{FunSuiteLike, Matchers}

class DeferAsyncTestSpec extends TestKit(ActorSystem("DeferAsync", config))
                                 with ImplicitSender
                                 with FunSuiteLike
                                 with Matchers {
  test("deferAsync will execute right after event persisted") {
    val testee = system.actorOf(Props(classOf[Foo]))
    testee ! "action1"
    testee ! "action2"

    expectMsgPF() {
      case "evt-action1-1" ⇒
    }
    expectMsgPF() {
      case "evt-action1-2" ⇒
    }
    expectMsgPF(){
      case "evt-action2-1"⇒
    }

    expectMsgPF(){
      case "evt-action2-2"⇒
    }

  }
}

class Foo extends PersistentActor {
  override def journalPluginId: String = "inmem"

  override def receiveRecover: Receive = {
    case _ ⇒
  }

  override def receiveCommand = {
    case c: String ⇒
      persist(s"evt-$c-1") { event ⇒
        Thread.sleep(100)
        sender ! event
      }
      deferAsync(s"evt-$c-2") { event ⇒
        sender ! event
      }
  }

  override def persistenceId = "foo-id"

  override def recovery: Recovery = Recovery.none
}
