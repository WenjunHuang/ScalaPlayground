package eventbus

import akka.actor.{ActorRef, ActorSystem}
import akka.event.{EventBus, LookupClassification, SubchannelClassification}
import akka.testkit.TestKit
import akka.util.Subclassification
import org.scalatest._

class StartsWithSubclassification extends Subclassification[String]{
  override def isEqual(x: String, y: String) = x == y

  override def isSubclass(x: String, y: String) = x.startsWith(y)
}

final case class MsgEnvelope(topic: String, payload: Any)

class LookupBusImpl extends EventBus with LookupClassification {
  override type Event = MsgEnvelope
  override type Classifier = String
  override type Subscriber = ActorRef

  override protected def mapSize(): Int = 128

  override protected def compareSubscribers(a: Subscriber, b: Subscriber): Int =
    a.compareTo(b)

  override protected def classify(event: Event): Classifier = event.topic

  override protected def publish(event: Event, subscriber: Subscriber): Unit =
    subscriber ! event.payload
}

class SubchannelBusImpl extends EventBus with SubchannelClassification{
  override type Event = MsgEnvelope
  override type Classifier = String
  override type Subscriber = ActorRef

  override protected implicit def subclassification: Subclassification[Classifier] =
    new StartsWithSubclassification

  override protected def classify(event: MsgEnvelope): Classifier = event.topic

  override protected def publish(event: MsgEnvelope, subscriber: ActorRef): Unit =
    subscriber ! event.payload
}

class EventBusTest extends TestKit(ActorSystem("EventBusTest")) with FunSuiteLike with Matchers {
  test("should receive correct event") {
    val lookupBus = new LookupBusImpl
    lookupBus.subscribe(testActor,"greetings")
    lookupBus.publish(MsgEnvelope("time",System.currentTimeMillis()))
    lookupBus.publish(MsgEnvelope("greetings","hello"))
    expectMsg("hello")
  }

  test("subchannel should work"){
    val subchannelBus = new SubchannelBusImpl
    subchannelBus.subscribe(testActor,"abc")
    subchannelBus.publish(MsgEnvelope("xyzabc","x"))
    subchannelBus.publish(MsgEnvelope("bcdef","b"))
    subchannelBus.publish(MsgEnvelope("abc","c"))
    expectMsg("c")

    subchannelBus.publish(MsgEnvelope("abcdef","d"))
    expectMsg("d")
  }
}
