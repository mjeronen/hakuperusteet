package fi.vm.sade.hakuperusteet

import java.io.File
import java.net.InetSocketAddress

import com.sun.net.httpserver.{HttpServer, HttpExchange, HttpHandler}
import fi.vm.sade.hakuperusteet.db.HsqlDatabase
import org.eclipse.jetty.webapp.WebAppContext
import org.slf4j.LoggerFactory

import scala.sys.process.{Process, ProcessIO}

class HakuperusteetTestServer extends HakuperusteetServer {
  override def setCookieParams(context: WebAppContext) {}
}

object HakuperusteetTestServer {
  val logger = LoggerFactory.getLogger(this.getClass)

  /*
   * ./sbt "test:run-main fi.vm.sade.hakuperusteet.HakuperusteetTestServer"
   * ./sbt "test:run-main fi.vm.sade.hakuperusteet.HakuperusteetTestServer" -J-DuseHsql=true
   */
  def main(args: Array[String]): Unit = {
    startMockServer()
    val useHsqldb = System.getProperty("useHsql", "false") == "true"
    if (useHsqldb) {
      val hsqlDb = new HsqlDatabase("jdbc:hsqldb:mem:hakuperusteet", "sa", "")
      hsqlDb.startHsqlServer()
      startCommandServer(hsqlDb)
    }
    val s = new HakuperusteetTestServer
    s.runServer()
    logger.info("Started HakuperusteetTestServer")
  }

  private def startMockServer() {
    val pb = Process(Seq("node", "server.js"), new File("./mockserver/"), "PORT" -> "3001", "LDAP_PORT" -> "1390")
    val pio = new ProcessIO(_ => (), stdout => scala.io.Source.fromInputStream(stdout).getLines.foreach(println), _ => ())
    pb.run(pio)
  }

  private def startCommandServer(db: HsqlDatabase) {
    val server = HttpServer.create(new InetSocketAddress(8000), 0)
    server.createContext("/testoperation/reset", new ResetHandler(db))
    server.setExecutor(null)
    server.start
  }
}

class ResetHandler(val db: HsqlDatabase) extends HttpHandler {
  override def handle(t: HttpExchange) = {
    println("HSQLDB reset!")
    db.resetDb
    val response = "OK"
    t.sendResponseHeaders(200, response.length)
    val os = t.getResponseBody
    os.write(response.getBytes)
    os.close()
  }
}
