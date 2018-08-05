package persistent

import akka.actor.{ActorSystem, Props}
import akka.persistence.PersistentActor
import com.typesafe.config.ConfigFactory
import persistent.PersistentFoo.ModifyName

object PersistentFoo {

  case class ModifyName(name: String)

}

trait UsingSimpleJournal extends PersistentActor {
  override def journalPluginId: String = "akka.persistence.journal.simple"
}

trait UsingInmenJournal extends PersistentActor {
  override def journalPluginId: String = "akka.persistence.journal.inmem"
}

class PersistentFoo(id: String, var name: String) extends PersistentActor {
  override def receiveRecover = {
    case _ ⇒
  }

  override def receiveCommand = {
    case e: ModifyName ⇒
      persist(e) { e ⇒
        name = e.name
        println("after modify name")
      }
      println("on receive modifyname")
  }

  override def persistenceId = id
}

object Main extends App {
  implicit val system = ActorSystem("Foo", ConfigFactory.parseString(
    """
      |akka.persistence.journal.simple {
      |    # Class name of the plugin.
      |    class = "persistent.SimpleJournalPlugin"
      |    # Dispatcher for the plugin actor.
      |    plugin-dispatcher = "akka.actor.default-dispatcher"
      |}
    """.stripMargin).withFallback(ConfigFactory.load()))

  val p = Props(new PersistentFoo("id","my plugin") with UsingSimpleJournal)
  val simple = system.actorOf(p)
  simple ! ModifyName("huangwenjun")

  sys.addShutdownHook(system.terminate())
}
