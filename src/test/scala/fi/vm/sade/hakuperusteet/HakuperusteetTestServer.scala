package fi.vm.sade.hakuperusteet

import org.slf4j.LoggerFactory

object HakuperusteetTestServer {
  val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    val s = new HakuperusteetServer
    s.runServer()
    logger.info("Started HakuperusteetTestServer")
  }
}