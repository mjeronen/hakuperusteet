package fi.vm.sade.hakuperusteet.db

import java.sql.DriverManager
import java.util.Properties

import org.flywaydb.core.Flyway

class HsqlDatabase(val db: String, val user: String, val password: String) {
  def startHsqlServer() {
    Class.forName("org.hsqldb.jdbc.JDBCDriver")
    val props = createConnectionProperties
    DriverManager.getConnection(db, props)
    migrateDb
  }

  def createConnectionProperties = {
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
    props
  }

  private def migrateDb {
    val flyway = new Flyway
    flyway.setDataSource(db, user, password)
    flyway.setValidateOnMigrate(false)
    flyway.migrate
  }
}
