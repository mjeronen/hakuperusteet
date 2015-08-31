package fi.vm.sade.hakuperusteet.db

import java.util.Date

import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.LazyLogging
import fi.vm.sade.hakuperusteet.User
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase.DB
import fi.vm.sade.hakuperusteet.db.generated.Tables
import fi.vm.sade.hakuperusteet.db.generated.Tables.UserRow
import slick.driver.PostgresDriver
import slick.driver.PostgresDriver.api._
import slick.util.AsyncExecutor
import org.flywaydb.core.Flyway

import scala.concurrent.duration.Duration
import scala.concurrent.Await

case class HakuperusteetDatabase(db: DB) {
  implicit class RunAndAwait[R](r: slick.dbio.DBIOAction[R, slick.dbio.NoStream, Nothing]) {
    def run: R = Await.result(db.run(r), Duration.Inf)
  }

  def findUser(email: String): Option[User] =
    Tables.User.filter(_.email === email).result.headOption.run.map((u) => User(u.henkiloOid, u.email, u.firstname, u.lastname, u.birthdate, u.personid, u.idpentity, u.gender, u.nationality, u.educationLevel, u.educationCountry))

  def insertUser(user: User) = {
    val useAutoIncrementId = 0
    val newUserRow = UserRow(useAutoIncrementId, user.personId, user.email, user.idpentityid, user.firstName, user.lastName, user.gender,
      new java.sql.Date(user.birthDate.getTime), user.personId, user.nationality, user.educationLevel, user.educationCountry)
    val y = (Tables.User returning Tables.User) += newUserRow
    y.run
  }
}

object HakuperusteetDatabase extends LazyLogging {
  type DB = PostgresDriver.backend.DatabaseDef

  def init(config: Config)(implicit executor: AsyncExecutor): HakuperusteetDatabase = {
    val url = config.getString("hakuperusteet.db.url")
    val user = config.getString("hakuperusteet.db.username")
    val password = config.getString("hakuperusteet.db.password")
    migrateSchema(url, user, password)
    HakuperusteetDatabase(Database.forURL(url = url, user = user, password = password, executor = executor))
  }

  private def migrateSchema(url: String, user: String, password: String) = {
    try {
      val flyway = new Flyway
      flyway.setDataSource(url, user, password)
      flyway.setSchemas("hakuperusteet")
      flyway.setValidateOnMigrate(false)
      //flyway.clean // removeMe
      flyway.migrate
    } catch {
      case e: Exception => logger.error("Migration failure", e)
    }
  }
}
