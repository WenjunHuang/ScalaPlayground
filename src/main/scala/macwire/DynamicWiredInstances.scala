package macwire

trait DatabaseConnector
class MysqlDatabaseConnector extends DatabaseConnector

class SecurityFilter()

class MyApp {
  def securityFilter = new SecurityFilter()
  val databaseConnector = new MysqlDatabaseConnector
}


import com.softwaremill.macwire._

object DynamicWiredInstances extends App{
  val wired = wiredInModule(new MyApp)

  wired.lookup(classOf[SecurityFilter])

  wired.lookup(classOf[DatabaseConnector])

}

