package fi.vm.sade.hakuperusteet

import java.io.File

import fi.vm.sade.hakuperusteet.db.{GlobalExecutionContext, HakuperusteetDatabase}
import fi.vm.sade.hakuperusteet.domain.{ApplicationObjects, Users}
import fi.vm.sade.hakuperusteet.util.ConfigUtil
import slick.util.AsyncExecutor

import scala.sys.process.{Process, ProcessIO}

object HakuperusteetAdminTestServer {

  /*
   * ./sbt "test:run-main fi.vm.sade.hakuperusteet.HakuperusteetAdminTestServer"
   */
  def main(args: Array[String]): Unit = {
    ConfigUtil.writeConfigFile(EmbeddedPostgreSql.configAsMap)

    implicit val executor = GlobalExecutionContext.context
    implicit val asyncExecutor: AsyncExecutor = GlobalExecutionContext.asyncExecutor
    EmbeddedPostgreSql.startEmbeddedPostgreSql
    startMockServer()
    // Generate test data
    val db = HakuperusteetDatabase.init(Configuration.props)

    val userAndApplication = Users.generateUsers.map(u => (u, ApplicationObjects.generateApplicationObject(u)))
    userAndApplication.foreach{case (user, applicationObject) =>
      db.upsertUser(user)
      db.upsertApplicationObject(applicationObject)}


    val s = new HakuperusteetAdminServer
    s.runServer()

  }

  private def startMockServer() {
    val pb = Process(Seq("node", "server.js"), new File("./mockserver/"), "PORT" -> "3001", "LDAP_PORT" -> "1390")
    val pio = new ProcessIO(_ => (), stdout => scala.io.Source.fromInputStream(stdout).getLines.foreach(println), _ => ())
    pb.run(pio)
  }

}
