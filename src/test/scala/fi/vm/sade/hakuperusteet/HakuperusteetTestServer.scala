package fi.vm.sade.hakuperusteet

import java.io.File
import java.net.InetSocketAddress

import com.sun.net.httpserver.{HttpServer, HttpExchange, HttpHandler}
import com.typesafe.config.Config
import fi.vm.sade.hakuperusteet.HakuperusteetAdminTestServer._
import fi.vm.sade.hakuperusteet.db.{GlobalExecutionContext, HakuperusteetDatabase}
import fi.vm.sade.hakuperusteet.util.ConfigUtil
import org.eclipse.jetty.webapp.WebAppContext
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import slick.util.AsyncExecutor

import scala.sys.process.{Process, ProcessIO}

class HakuperusteetTestServer extends HakuperusteetServer {
  override def setSecureCookieParams(context: WebAppContext) {}
}

object HakuperusteetTestServer {
  val logger = LoggerFactory.getLogger(this.getClass)
  val isEmbeddedConfig = System.getProperty("embedded", "false") == "true"
  var hakuperusteetTestServer:HakuperusteetTestServer = null

  /*
   * ./sbt "test:run-main fi.vm.sade.hakuperusteet.HakuperusteetTestServer"
   */
  def main(args: Array[String]): Unit = {
    if (isEmbeddedConfig) {
      logger.info("Using embedded PostgreSQL")
      ConfigUtil.writeConfigFile(EmbeddedPostgreSql.configAsMap)
      EmbeddedPostgreSql.startEmbeddedPostgreSql
    }
    startMockServer()
    startCommandServer()
    hakuperusteetTestServer = new HakuperusteetTestServer
    hakuperusteetTestServer.runServer()
    logger.info("Started HakuperusteetTestServer")
  }

  private def startMockServer() {
    val pb = Process(Seq("node", "server.js"), new File("./mockserver/"), "PORT" -> "3001", "LDAP_PORT" -> "1390")
    val pio = new ProcessIO(_ => (), stdout => scala.io.Source.fromInputStream(stdout).getLines.foreach(println), _ => ())
    pb.run(pio)
  }

  private def startCommandServer() {
    val server = HttpServer.create(new InetSocketAddress(8000), 0)
    server.createContext("/testoperation/reset", new ResetHandler())
    server.setExecutor(null)
    server.start
  }

  def cleanDB(): Unit = {
    val config = Configuration.props
    val url = config.getString("hakuperusteet.db.url")
    val user = config.getString("hakuperusteet.db.username")
    val password = config.getString("hakuperusteet.db.password")
    val flyway = new Flyway
    flyway.setDataSource(url, user, password)
    flyway.clean
    flyway.migrate
    hakuperusteetTestServer.restart
  }
}

class ResetHandler() extends HttpHandler {
  implicit val executor = GlobalExecutionContext.context
  implicit val asyncExecutor: AsyncExecutor = GlobalExecutionContext.asyncExecutor
  override def handle(t: HttpExchange) = {
    HakuperusteetTestServer.cleanDB()
    val response = "OK"
    t.getResponseHeaders.add("Access-Control-Allow-Origin", "*")
    t.sendResponseHeaders(200, response.length)
    val os = t.getResponseBody
    os.write(response.getBytes)
    os.close()
  }
}
