package fi.vm.sade.hakuperusteet

import java.io.File
import java.sql.DriverManager
import java.util.Properties

import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory

import scala.sys.process.{Process, ProcessIO}

object HakuperusteetTestServer {
  val logger = LoggerFactory.getLogger(this.getClass)
  val db = "jdbc:hsqldb:mem:hakuperusteet"
  val user = "sa"
  val password = ""

  /*
   * ./sbt "test:run-main fi.vm.sade.hakuperusteet.HakuperusteetTestServer"
   * ./sbt "test:run-main fi.vm.sade.hakuperusteet.HakuperusteetTestServer" -J-DuseHsql=true
   */
  def main(args: Array[String]): Unit = {
    startMockServer()
    val useHsqldb = System.getProperty("useHsql", "false") == "true"
    if (useHsqldb) {
      startHsqlServer()
    }
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

  private def startHsqlServer() {
    Class.forName("org.hsqldb.jdbc.JDBCDriver")

    val props = new Properties
    props.put("user", user)
    props.put("password", password)
    props.put("sql.syntax_pgs", "true")
    props.put("hsqldb.applog", "3")
    props.put("sql.ignore_case", "true")
    props.put("shutdown", "true")
    props.put("sql.enforce_names", "true")
    props.put("sql.regular_names", "true")
    props.put("sql.enforce_refs", "true")

    DriverManager.getConnection(db, props)
    migrateDb
    createDbClasses
  }

  def migrateDb {
    val flyway = new Flyway
    flyway.setDataSource(db, user, password)
    flyway.setValidateOnMigrate(false)
    flyway.migrate
  }

  def createDbClasses {
    slick.codegen.SourceCodeGenerator.main(Array(
      "slick.driver.HsqldbDriver",
      "org.hsqldb.jdbc.JDBCDriver", db, "src/main/scala", "fi.vm.sade.hakuperusteet.db.generated", user, password)
    )
  }
}
