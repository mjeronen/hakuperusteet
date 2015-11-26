package fi.vm.sade.hakuperusteet.db

import java.sql
import java.sql.Timestamp
import java.util.Calendar

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.admin.SynchronizationStatus
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase.DB
import fi.vm.sade.hakuperusteet.db.generated.Tables
import fi.vm.sade.hakuperusteet.db.generated.Tables._
import fi.vm.sade.hakuperusteet.domain.{ApplicationObject, Payment, User, _}
import org.flywaydb.core.Flyway
import slick.driver.PostgresDriver
import slick.driver.PostgresDriver.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

case class HakuperusteetDatabase(db: DB) extends LazyLogging {
  import HakuperusteetDatabase._

  implicit class RunAndAwait[R](r: slick.dbio.DBIOAction[R, slick.dbio.NoStream, Nothing]) {
    def run: R = Await.result(db.run(r), Duration.Inf)
  }
  val useAutoIncrementId = 0

  def findUser(email: String): Option[User] = {
    // join Tables.UserDetails on (_.id === _.id)
    (Tables.User.filter(_.email === email) joinLeft Tables.UserDetails on (_.id === _.id)).result.headOption.run.map(userRowToUser)
  }

  def findUserByOid(henkiloOid: String): Option[User] =
    (Tables.User.filter(_.henkiloOid === henkiloOid) joinLeft Tables.UserDetails on (_.id === _.id)).result.headOption.run.map(userRowToUser)

  def allUsers: Seq[User] = (Tables.User joinLeft Tables.UserDetails on (_.id === _.id)).result.run.map(userRowToUser)

  def upsertPartialUser(partialUser: User): Option[User] = {
    val upsertedUser = (Tables.User returning Tables.User).insertOrUpdate(partialUserToUserRow(partialUser)).run
    upsertedUser match {
      case Some(r) => Some(User.partialUser(Some(r.id), r.henkiloOid, r.email, IDPEntityId.withName(r.idpentityid), r.uilang))
      case None => None
    }
  }

  def upsertUser(user: User): Option[User] = {
    val (u,d) = userToUserRow(user)
    try {
      val upsertedUser = (Tables.User returning Tables.User).insertOrUpdate(u).run
      upsertedUser match {
        case Some(newUser) => {
          val withCopiedId = d.copy(id = newUser.id)
          val upsertedUserDetails = (Tables.UserDetails returning Tables.UserDetails).insertOrUpdate(withCopiedId).run
          Some(userRowToUser(newUser,upsertedUserDetails))
        }
        case None => None
      }
    } catch {
      case e : Throwable => {
        logger.error("Upserting user failed!", e)
        throw e
      }
    }
  }

  def findApplicationObjects(user: User): Seq[ApplicationObject] =
    Tables.ApplicationObject.filter(_.henkiloOid === user.personOid).result.run.map(aoRowToAo)

  def findApplicationObjectByHakukohdeOid(user: User, hakukohdeOid: String) =
    Tables.ApplicationObject.filter(_.henkiloOid === user.personOid).filter(_.hakukohdeOid === hakukohdeOid).result.headOption.run.map(aoRowToAo)

  def upsertApplicationObject(applicationObject: ApplicationObject) =
    (Tables.ApplicationObject returning Tables.ApplicationObject).insertOrUpdate(aoToAoRow(applicationObject)).run.map(aoRowToAo)

  def findPaymentByHenkiloOidAndHakemusOid(henkiloOid: String, hakemusOid: String): Option[Payment] =
    Tables.Payment.filter(_.henkiloOid === henkiloOid).filter(_.hakemusOid === hakemusOid).sortBy(_.tstamp.desc).result.headOption.run.map(paymentRowToPayment)

  def findPaymentByOrderNumber(user: User, orderNumber: String): Option[Payment] =
    Tables.Payment.filter(_.henkiloOid === user.personOid).filter(_.orderNumber === orderNumber).sortBy(_.tstamp.desc).result.headOption.run.map(paymentRowToPayment)

  def findPayments(user: User): Seq[Payment] =
    Tables.Payment.filter(_.henkiloOid === user.personOid).sortBy(_.tstamp.desc).result.run.map(paymentRowToPayment)

  def upsertPayment(payment: Payment): Option[Payment] =
    (Tables.Payment returning Tables.Payment).insertOrUpdate(paymentToPaymentRow(payment)).run.map(paymentRowToPayment)

  def nextOrderNumber() = sql"select nextval('#$schemaName.ordernumber');".as[Int].run.head

  def insertSyncRequest(user: User, ao: ApplicationObject) = (Tables.Synchronization returning Tables.Synchronization).insertOrUpdate(
    SynchronizationRow(useAutoIncrementId, now, user.personOid.get, Some(ao.hakuOid), Some(ao.hakukohdeOid), SynchronizationStatus.todo.toString, None, None)).run

  def insertPaymentSyncRequest(user:User, payment: Payment) = (Tables.Synchronization returning Tables.Synchronization).insertOrUpdate(
    SynchronizationRow(useAutoIncrementId, now, user.personOid.get, None, None, SynchronizationStatus.todo.toString, None, payment.hakemusOid)).run

  def markSyncDone(id: Int): Unit = findSynchronizationRow(id).foreach(markSyncDone)

  def markSyncDone(row: SynchronizationRow) = updateSyncRequest(row.copy(updated = Some(now), status = SynchronizationStatus.done.toString))

  def markSyncError(id: Int): Unit = findSynchronizationRow(id).foreach(markSyncError)

  def markSyncError(row: SynchronizationRow) = updateSyncRequest(row.copy(updated = Some(now), status = SynchronizationStatus.error.toString))

  private def updateSyncRequest(row: SynchronizationRow) = (Tables.Synchronization returning Tables.Synchronization).insertOrUpdate(row).run

  def fetchNextSyncIds: Seq[Int] = sql"update synchronization set status = '#${SynchronizationStatus.active.toString}' where id in (select id from synchronization where status = '#${SynchronizationStatus.todo.toString}' or status = '#${SynchronizationStatus.error.toString}' order by status desc, created asc limit 1) returning ( id );".as[Int].run

  def findSynchronizationRequestsForIds(ids: Seq[Int]): Seq[Option[SyncRequest]] = ids.flatMap(findSynchronizationRequest(_))

  private def convertApplicationObjectSyncRequest(row: SynchronizationRow) = {
    (row.hakuOid,row.hakukohdeOid) match {
      case (Some(hakuOid), Some(hakukohdeOid)) => Some(ApplicationObjectSyncRequest(row.id,row.henkiloOid, hakuOid,hakukohdeOid))
      case _ => None
    }
  }
  private def convertHakuAppSyncRequest(row: SynchronizationRow) = {
    (row.hakemusOid) match {
      case Some(hakemusOid) => Some(HakuAppSyncRequest(row.id, row.henkiloOid,hakemusOid))
      case _ => None
    }
  }
  def synchronizationRowToSyncRequest(r: Tables.SynchronizationRow) = convertApplicationObjectSyncRequest(r).orElse(convertHakuAppSyncRequest(r))
  def findSynchronizationRow(id: Int): Seq[Tables.SynchronizationRow] = Tables.Synchronization.filter(_.id === id).result.run
  def findSynchronizationRequest(id: Int): Seq[Option[SyncRequest]] = findSynchronizationRow(id).map(synchronizationRowToSyncRequest)

  private def paymentToPaymentRow(payment: Payment) =
    PaymentRow(payment.id.getOrElse(useAutoIncrementId), payment.personOid, new Timestamp(payment.timestamp.getTime), payment.reference, payment.orderNumber, payment.status.toString, payment.paymCallId, payment.hakemusOid)

  private def paymentRowToPayment(r: PaymentRow) =
    Payment(Some(r.id), r.henkiloOid, r.tstamp, r.reference, r.orderNumber, r.paymCallId, PaymentStatus.withName(r.status), r.hakemusOid)

  private def aoRowToAo(r: ApplicationObjectRow) = ApplicationObject(Some(r.id), r.henkiloOid, r.hakukohdeOid, r.hakuOid, r.educationLevel, r.educationCountry)

  private def aoToAoRow(e: ApplicationObject) = ApplicationObjectRow(e.id.getOrElse(useAutoIncrementId), e.personOid, e.hakukohdeOid, e.educationLevel, e.educationCountry, e.hakuOid)

  private def partialUserToUserRow(u: User): Tables.UserRow = {
    val id = u.id.getOrElse(useAutoIncrementId)
    UserRow(id, u.personOid, u.email, u.idpentityid.toString, u.uiLang)
  }

  private def userToUserRow(u: User): (Tables.UserRow, Tables.UserDetailsRow) = {
    val id = u.id.getOrElse(useAutoIncrementId)
    (UserRow(id, u.personOid, u.email, u.idpentityid.toString, u.uiLang), UserDetailsRow(id, u.firstName.get,
      u.lastName.get, u.gender.get, new sql.Date(u.birthDate.get.getTime), u.personId, u.nativeLanguage.get, u.nationality.get))
  }

  private def userRowToUser(u: (Tables.UserRow, Option[Tables.UserDetailsRow])) = {
    val r = u._1
    (u._2) match {
      case Some(details) => userRowAndDetailsToUser(r, details)
      case _ => User.partialUser(Some(r.id), r.henkiloOid, r.email, IDPEntityId.withName(r.idpentityid), r.uilang)
    }
  }

  private def userRowAndDetailsToUser(r: Tables.UserRow, d: Tables.UserDetailsRow): User =
   User(Some(r.id), r.henkiloOid, r.email, Some(d.firstname), Some(d.lastname), Some(d.birthdate), d.personid, IDPEntityId.withName(r.idpentityid), Some(d.gender), Some(d.nativeLanguage), Some(d.nationality), r.uilang)

  private def now = new java.sql.Timestamp(Calendar.getInstance.getTime.getTime)
}

object HakuperusteetDatabase extends LazyLogging {
  type DB = PostgresDriver.backend.DatabaseDef
  val schemaName = "public"
  val inited = scala.collection.mutable.HashMap.empty[Config, HakuperusteetDatabase]

  def init(config: Config): HakuperusteetDatabase = {
    this.synchronized {
      val url = config.getString("hakuperusteet.db.url")
      val user = config.getString("hakuperusteet.db.user")
      val password = config.getString("hakuperusteet.db.password")
      if(!inited.contains(config)) {
        if(inited.nonEmpty) {
          throw new IllegalArgumentException("You're doing it wrong. For some reason DB config has changed.");
        }
        migrateSchema(url, user, password)
        val db = HakuperusteetDatabase(Database.forConfig("hakuperusteet.db", config))
        inited += (config -> db)
      }
      inited(config)
    }
  }

  def close = inited.values.foreach(_.db.close)

  private def migrateSchema(url: String, user: String, password: String) = {
    try {
      val flyway = new Flyway
      flyway.setDataSource(url, user, password)
      flyway.setSchemas(schemaName)
      flyway.setValidateOnMigrate(false)
      flyway.migrate
    } catch {
      case e: Exception => logger.error("Migration failure", e)
        System.exit(1)
    }
  }
}
