package wiki

import io.vertx.core.json.JsonObject
import io.vertx.lang.scala.ScalaVerticle
import io.vertx.scala.ext.jdbc.JDBCClient
import io.vertx.scala.ext.web.Router
import org.slf4j.LoggerFactory

import scala.concurrent.{Future, Promise}

class MainVerticle extends ScalaVerticle {
  val logger = LoggerFactory.getLogger(classOf[MainVerticle])
  private val SQL_CREATE_PAGES_TABLE = "create table if not exists Pages (Id integer identity primary key, Name varchar(255) unique, Content clob)"
  private val SQL_GET_PAGE = "select Id, Content from Pages where Name = ?" (1)
  private val SQL_CREATE_PAGE = "insert into Pages values (NULL, ?, ?)"
  private val SQL_SAVE_PAGE = "update Pages set Content = ? where Id = ?"
  private val SQL_ALL_PAGES = "select Name from Pages"
  private val SQL_DELETE_PAGE = "delete from Pages where Id = ?"

  override def startFuture(): Future[Unit] = {
    prepareDatabase()
      .flatMap { _ ⇒ startHttpServer() }
  }

  def prepareDatabase(): Future[Unit] = {
    val promise = Promise[Unit]()
    val dbClient = JDBCClient.createShared(vertx, new JsonObject()
      .put("url", "jdbc:hsqldb:file:db/wiki")
      .put("driver_class", "org.hsqldb.jdbcDriver")
      .put("max_pool_size", 30))
    dbClient.getConnection { ar ⇒
      if (ar.failed()) {
        logger.error("Could not open a database connection",ar.cause())
        promise.failure(ar.cause())
      }
      else {
        val connection = ar.result()
        connection.execute(SQL_CREATE_PAGES_TABLE, {create⇒
          connection.close()
          if (create.failed()){
            logger.error("Database preparation error",create.cause())
            promise.failure(create.cause())
          }else {
            promise.success()
          }
        })
      }
    }

    promise.future
  }

  def startHttpServer(): Future[Unit] = {
    val router = Router.router(vertx)
    router.get("/hello")
      .handler(_.response().end("world"))

    vertx.createHttpServer()
      .requestHandler(router.accept _)
      .listenFuture(8666, "0.0.0.0")
      .map(_ ⇒ ())
  }

}
