package fi.vm.sade.hakuperusteet.db

import java.sql.Timestamp
import java.util.Date

import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.LazyLogging
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase.DB
import fi.vm.sade.hakuperusteet.db.generated.Tables
import fi.vm.sade.hakuperusteet.db.generated.Tables.{SessionRow, PaymentRow, UserRow}
import fi.vm.sade.hakuperusteet.domain.{Session, PaymentStatus, Payment, User}
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
  val useAutoIncrementId = 0

  def findSession(email: String): Option[Session] =
    Tables.Session.filter(_.email === email).result.headOption.run.map(sessionRowToSession)

  def upsertSession(session: Session): Option[Session] =
    (Tables.Session returning Tables.Session).insertOrUpdate(sessionToSessionRow(session)).run.map(sessionRowToSession)

  def findUser(email: String): Option[User] =
    Tables.User.filter(_.email === email).result.headOption.run.map((u) => User(Some(u.id), u.henkiloOid, u.email, u.firstname, u.lastname, u.birthdate, u.personid, u.idpentityid, u.gender, u.nationality, u.educationLevel, u.educationCountry))

  def insertUser(user: User) = {
    val newUserRow = UserRow(useAutoIncrementId, user.personOid, user.email, user.idpentityid, user.firstName, user.lastName, user.gender,
      new java.sql.Date(user.birthDate.getTime), user.personId, user.nationality, user.educationLevel, user.educationCountry)
    val y = (Tables.User returning Tables.User) += newUserRow
    y.run
  }

  def findPayment(user: User): Option[Payment] =
    Tables.Payment.filter(_.henkiloOid === user.personOid).result.headOption.run.map((r) => Payment(Some(r.id), r.henkiloOid.get, r.tstamp, r.reference, r.orderNumber, PaymentStatus.withName(r.status)))

  def insertPayment(user: User, payment: Payment) = {
    val y = (Tables.Payment returning Tables.Payment) += paymentToPaymentRow(user.personOid, payment)
    y.run
  }

  def updatePayment(payment: Payment) = {
    Tables.Payment.update(paymentToPaymentRow(Some(payment.personOid), payment)).run
  }

  private def paymentToPaymentRow(personOid: Option[String], payment: Payment) =
    PaymentRow(payment.id.getOrElse(useAutoIncrementId), personOid, new Timestamp(payment.timestamp.getTime), payment.reference, payment.orderNumber, payment.status.toString)

  private def sessionToSessionRow(session: Session): Tables.SessionRow =
    SessionRow(session.id.getOrElse(useAutoIncrementId), session.email, session.token, session.idpentityid)

  private def sessionRowToSession(r: Tables.SessionRow): Session = Session(Some(r.id), r.email, r.token, r.idpentityid)
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
