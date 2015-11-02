package fi.vm.sade.hakuperusteet

import java.net.InetSocketAddress

import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}
import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.domain.{ApplicationObjects, Users, Payments}

import scala.sys.process.{Process, ProcessIO}

object HakuperusteetAdminTestServer extends LazyLogging {

  /*
   * ./sbt "test:run-main fi.vm.sade.hakuperusteet.HakuperusteetAdminTestServer"
   */
  def main(args: Array[String]): Unit = {
    HakuperusteetTestServer.startMockServer()
    DBSupport.ensureEmbeddedIsStartedIfNeeded()
//    startCommandServer()
    initDB()

    val s = new HakuperusteetAdminServer
    s.runServer()
  }
  private def initDB() = {
    // Generate test data
    val db = HakuperusteetDatabase.init(Configuration.props)
    HakuperusteetTestServer.cleanDB()

    val userAndApplication = Users.generateUsers.map(u =>
      (u, ApplicationObjects.generateApplicationObject(u), Payments.generatePayments(u)))
    userAndApplication.foreach{case (user, applicationObjects, payments) =>
      val u = db.findUser(user.email)
      if(u.isEmpty) {
        db.upsertUser(user)
        applicationObjects.foreach(db.upsertApplicationObject)
        payments.foreach(db.upsertPayment)
      }}
  }

  private def startCommandServer() {
    val server = HttpServer.create(new InetSocketAddress(9000), 0)
    server.createContext("/testoperation/reset", new HttpHandler() {
      override def handle(t: HttpExchange) = {
        initDB()
        val response = "OK"
        t.getResponseHeaders.add("Access-Control-Allow-Origin", "*")
        t.sendResponseHeaders(200, response.length)
        val os = t.getResponseBody
        os.write(response.getBytes)
        os.close()
      }
    })
    server.setExecutor(null)
    server.start
  }
}
