package fi.vm.sade.hakuperusteet

import java.io.File
import java.net.InetSocketAddress
import java.sql.{Connection, DriverManager}

import com.sun.net.httpserver.{HttpServer, HttpExchange, HttpHandler}
import fi.vm.sade.hakuperusteet.util.ConfigUtil
import org.eclipse.jetty.webapp.WebAppContext
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer
import scala.sys.process.{Process, ProcessIO}

class HakuperusteetTestServer extends HakuperusteetServer {
  override def setSecureCookieParams(context: WebAppContext) {}
}

object HakuperusteetTestServer {
  val logger = LoggerFactory.getLogger(this.getClass)
  val isEmbeddedConfig = System.getProperty("embedded", "false") == "true"

  /*
   * ./sbt "test:run-main fi.vm.sade.hakuperusteet.HakuperusteetTestServer"
   */
  def main(args: Array[String]): Unit = {
    OptionalEmbeddedDB.embeddedDB
    startMockServer()
    startCommandServer()
    new HakuperusteetTestServer().runServer()
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

  private def getTables(jdbcConnection: Connection) = {
    val tables = new ListBuffer[String]()
    val dbmd = jdbcConnection.getMetaData()
    val rs = dbmd.getTables(null, "public", "%", Array("TABLE"))
    while (rs.next()) {
      tables += rs.getString("TABLE_NAME")
    }
    tables.toList
  }

  def cleanDB(): Unit = {
    val config = Configuration.props
    val url = config.getString("hakuperusteet.db.url")
    val user = config.getString("hakuperusteet.db.user")
    val password = config.getString("hakuperusteet.db.password")
    val jdbcConnection = DriverManager.getConnection(url, user, password)
    val tables = getTables(jdbcConnection)
    try {
      Array("synchronization", "application_object", "payment", "user","jettysessionids","jettysessions")
        .foreach(name => if(tables.contains(name)){
          jdbcConnection.createStatement.execute("DELETE from \"" + name + "\";")
        })
    } catch {
      case e: Exception => {
        e.printStackTrace()
      }
    }
  }
}

class ResetHandler() extends HttpHandler {
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
