package fi.vm.sade.hakuperusteet

import java.io.File
import java.net.InetSocketAddress

import com.sun.net.httpserver.{HttpServer, HttpExchange, HttpHandler}
import fi.vm.sade.hakuperusteet.db.{GlobalExecutionContext, HakuperusteetDatabase}
import org.eclipse.jetty.webapp.WebAppContext
import org.slf4j.LoggerFactory
import slick.util.AsyncExecutor

import scala.sys.process.{Process, ProcessIO}

class HakuperusteetTestServer extends HakuperusteetServer {
  override def setCookieParams(context: WebAppContext) {}
}

object HakuperusteetTestServer {
  val logger = LoggerFactory.getLogger(this.getClass)

  /*
   * ./sbt "test:run-main fi.vm.sade.hakuperusteet.HakuperusteetTestServer"
   */
  def main(args: Array[String]): Unit = {
    EmbeddedPostgreSql.startEmbeddedPostgreSql
    startMockServer()
    startCommandServer()
    val s = new HakuperusteetTestServer
    s.runServer()
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
}

class ResetHandler() extends HttpHandler {
  implicit val executor = GlobalExecutionContext.context
  implicit val asyncExecutor: AsyncExecutor = GlobalExecutionContext.asyncExecutor
  override def handle(t: HttpExchange) = {
    HakuperusteetDatabase.init(Configuration.props)
    val response = "OK"
    t.getResponseHeaders.add("Access-Control-Allow-Origin", "*")
    t.sendResponseHeaders(200, response.length)
    val os = t.getResponseBody
    os.write(response.getBytes)
    os.close()
  }
}
