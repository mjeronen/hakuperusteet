package fi.vm.sade.hakuperusteet.db

import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.LazyLogging
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

  def findUser(email: String): Option[UserRow] = {
    val q = Tables.User.filter(_.email === email)
    q.result.headOption.run
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
      flyway.migrate
    } catch {
      case e: Exception => logger.error("Migration failure", e)
    }
  }
}
