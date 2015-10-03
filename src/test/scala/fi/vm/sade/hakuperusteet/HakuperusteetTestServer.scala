package fi.vm.sade.hakuperusteet

import java.io.File

import org.slf4j.LoggerFactory

import scala.sys.process.{Process, ProcessIO}

object HakuperusteetTestServer {
  val logger = LoggerFactory.getLogger(this.getClass)

  /*
   * ./sbt "test:run-main fi.vm.sade.hakuperusteet.HakuperusteetTestServer"
   */
  def main(args: Array[String]): Unit = {
    startMockServer()
    val s = new HakuperusteetServer
    s.runServer()
    logger.info("Started HakuperusteetTestServer")
  }

  private def startMockServer() {
    val pb = Process(Seq("node", "server.js"), new File("./mockserver/"), "PORT" -> "3001", "LDAP_PORT" -> "1390")
    val pio = new ProcessIO(_ => (),
      stdout => scala.io.Source.fromInputStream(stdout).getLines.foreach(println),
      _ => ())
    pb.run(pio)
  }
}
