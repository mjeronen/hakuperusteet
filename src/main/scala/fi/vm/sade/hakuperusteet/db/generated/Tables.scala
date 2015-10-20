package fi.vm.sade.hakuperusteet.db.generated
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.driver.PostgresDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.driver.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Array(ApplicationObject.schema, Payment.schema, SchemaVersion.schema, Session.schema, Synchronization.schema, User.schema).reduceLeft(_ ++ _)
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table ApplicationObject
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param henkiloOid Database column henkilo_oid SqlType(varchar), Length(255,true)
   *  @param hakukohdeOid Database column hakukohde_oid SqlType(varchar), Length(255,true)
   *  @param educationLevel Database column education_level SqlType(varchar), Length(255,true)
   *  @param educationCountry Database column education_country SqlType(varchar), Length(255,true)
   *  @param hakuOid Database column haku_oid SqlType(varchar), Length(255,true) */
  case class ApplicationObjectRow(id: Int, henkiloOid: String, hakukohdeOid: String, educationLevel: String, educationCountry: String, hakuOid: String)
  /** GetResult implicit for fetching ApplicationObjectRow objects using plain SQL queries */
  implicit def GetResultApplicationObjectRow(implicit e0: GR[Int], e1: GR[String]): GR[ApplicationObjectRow] = GR{
    prs => import prs._
    ApplicationObjectRow.tupled((<<[Int], <<[String], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table application_object. Objects of this class serve as prototypes for rows in queries. */
  class ApplicationObject(_tableTag: Tag) extends Table[ApplicationObjectRow](_tableTag, "application_object") {
    def * = (id, henkiloOid, hakukohdeOid, educationLevel, educationCountry, hakuOid) <> (ApplicationObjectRow.tupled, ApplicationObjectRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(henkiloOid), Rep.Some(hakukohdeOid), Rep.Some(educationLevel), Rep.Some(educationCountry), Rep.Some(hakuOid)).shaped.<>({r=>import r._; _1.map(_=> ApplicationObjectRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column henkilo_oid SqlType(varchar), Length(255,true) */
    val henkiloOid: Rep[String] = column[String]("henkilo_oid", O.Length(255,varying=true))
    /** Database column hakukohde_oid SqlType(varchar), Length(255,true) */
    val hakukohdeOid: Rep[String] = column[String]("hakukohde_oid", O.Length(255,varying=true))
    /** Database column education_level SqlType(varchar), Length(255,true) */
    val educationLevel: Rep[String] = column[String]("education_level", O.Length(255,varying=true))
    /** Database column education_country SqlType(varchar), Length(255,true) */
    val educationCountry: Rep[String] = column[String]("education_country", O.Length(255,varying=true))
    /** Database column haku_oid SqlType(varchar), Length(255,true) */
    val hakuOid: Rep[String] = column[String]("haku_oid", O.Length(255,varying=true))

    /** Foreign key referencing User (database name education_henkilo_oid_fkey) */
    lazy val userFk = foreignKey("education_henkilo_oid_fkey", Rep.Some(henkiloOid), User)(r => r.henkiloOid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Index over (henkiloOid,hakukohdeOid) (database name education_henkilo_oid_hakukohde_oid_idx) */
    val index1 = index("education_henkilo_oid_hakukohde_oid_idx", (henkiloOid, hakukohdeOid))
  }
  /** Collection-like TableQuery object for table ApplicationObject */
  lazy val ApplicationObject = new TableQuery(tag => new ApplicationObject(tag))

  /** Entity class storing rows of table Payment
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param henkiloOid Database column henkilo_oid SqlType(varchar), Length(255,true)
   *  @param tstamp Database column tstamp SqlType(timestamptz)
   *  @param reference Database column reference SqlType(varchar), Length(255,true)
   *  @param orderNumber Database column order_number SqlType(varchar), Length(255,true)
   *  @param status Database column status SqlType(varchar), Length(255,true)
   *  @param paymCallId Database column paym_call_id SqlType(varchar), Length(255,true) */
  case class PaymentRow(id: Int, henkiloOid: String, tstamp: java.sql.Timestamp, reference: String, orderNumber: String, status: String, paymCallId: String)
  /** GetResult implicit for fetching PaymentRow objects using plain SQL queries */
  implicit def GetResultPaymentRow(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Timestamp]): GR[PaymentRow] = GR{
    prs => import prs._
    PaymentRow.tupled((<<[Int], <<[String], <<[java.sql.Timestamp], <<[String], <<[String], <<[String], <<[String]))
  }
  /** Table description of table payment. Objects of this class serve as prototypes for rows in queries. */
  class Payment(_tableTag: Tag) extends Table[PaymentRow](_tableTag, "payment") {
    def * = (id, henkiloOid, tstamp, reference, orderNumber, status, paymCallId) <> (PaymentRow.tupled, PaymentRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(henkiloOid), Rep.Some(tstamp), Rep.Some(reference), Rep.Some(orderNumber), Rep.Some(status), Rep.Some(paymCallId)).shaped.<>({r=>import r._; _1.map(_=> PaymentRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column henkilo_oid SqlType(varchar), Length(255,true) */
    val henkiloOid: Rep[String] = column[String]("henkilo_oid", O.Length(255,varying=true))
    /** Database column tstamp SqlType(timestamptz) */
    val tstamp: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("tstamp")
    /** Database column reference SqlType(varchar), Length(255,true) */
    val reference: Rep[String] = column[String]("reference", O.Length(255,varying=true))
    /** Database column order_number SqlType(varchar), Length(255,true) */
    val orderNumber: Rep[String] = column[String]("order_number", O.Length(255,varying=true))
    /** Database column status SqlType(varchar), Length(255,true) */
    val status: Rep[String] = column[String]("status", O.Length(255,varying=true))
    /** Database column paym_call_id SqlType(varchar), Length(255,true) */
    val paymCallId: Rep[String] = column[String]("paym_call_id", O.Length(255,varying=true))

    /** Foreign key referencing User (database name payment_henkilo_oid_fkey) */
    lazy val userFk = foreignKey("payment_henkilo_oid_fkey", Rep.Some(henkiloOid), User)(r => r.henkiloOid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)

    /** Index over (henkiloOid,orderNumber) (database name payment_henkilo_oid_order_number_idx) */
    val index1 = index("payment_henkilo_oid_order_number_idx", (henkiloOid, orderNumber))
    /** Index over (reference) (database name payment_reference_idx) */
    val index2 = index("payment_reference_idx", reference)
  }
  /** Collection-like TableQuery object for table Payment */
  lazy val Payment = new TableQuery(tag => new Payment(tag))

  /** Entity class storing rows of table SchemaVersion
   *  @param versionRank Database column version_rank SqlType(int4)
   *  @param installedRank Database column installed_rank SqlType(int4)
   *  @param version Database column version SqlType(varchar), PrimaryKey, Length(50,true)
   *  @param description Database column description SqlType(varchar), Length(200,true)
   *  @param `type` Database column type SqlType(varchar), Length(20,true)
   *  @param script Database column script SqlType(varchar), Length(1000,true)
   *  @param checksum Database column checksum SqlType(int4), Default(None)
   *  @param installedBy Database column installed_by SqlType(varchar), Length(100,true)
   *  @param installedOn Database column installed_on SqlType(timestamp)
   *  @param executionTime Database column execution_time SqlType(int4)
   *  @param success Database column success SqlType(bool) */
  case class SchemaVersionRow(versionRank: Int, installedRank: Int, version: String, description: String, `type`: String, script: String, checksum: Option[Int] = None, installedBy: String, installedOn: java.sql.Timestamp, executionTime: Int, success: Boolean)
  /** GetResult implicit for fetching SchemaVersionRow objects using plain SQL queries */
  implicit def GetResultSchemaVersionRow(implicit e0: GR[Int], e1: GR[String], e2: GR[Option[Int]], e3: GR[java.sql.Timestamp], e4: GR[Boolean]): GR[SchemaVersionRow] = GR{
    prs => import prs._
    SchemaVersionRow.tupled((<<[Int], <<[Int], <<[String], <<[String], <<[String], <<[String], <<?[Int], <<[String], <<[java.sql.Timestamp], <<[Int], <<[Boolean]))
  }
  /** Table description of table schema_version. Objects of this class serve as prototypes for rows in queries.
   *  NOTE: The following names collided with Scala keywords and were escaped: type */
  class SchemaVersion(_tableTag: Tag) extends Table[SchemaVersionRow](_tableTag, "schema_version") {
    def * = (versionRank, installedRank, version, description, `type`, script, checksum, installedBy, installedOn, executionTime, success) <> (SchemaVersionRow.tupled, SchemaVersionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(versionRank), Rep.Some(installedRank), Rep.Some(version), Rep.Some(description), Rep.Some(`type`), Rep.Some(script), checksum, Rep.Some(installedBy), Rep.Some(installedOn), Rep.Some(executionTime), Rep.Some(success)).shaped.<>({r=>import r._; _1.map(_=> SchemaVersionRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7, _8.get, _9.get, _10.get, _11.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column version_rank SqlType(int4) */
    val versionRank: Rep[Int] = column[Int]("version_rank")
    /** Database column installed_rank SqlType(int4) */
    val installedRank: Rep[Int] = column[Int]("installed_rank")
    /** Database column version SqlType(varchar), PrimaryKey, Length(50,true) */
    val version: Rep[String] = column[String]("version", O.PrimaryKey, O.Length(50,varying=true))
    /** Database column description SqlType(varchar), Length(200,true) */
    val description: Rep[String] = column[String]("description", O.Length(200,varying=true))
    /** Database column type SqlType(varchar), Length(20,true)
     *  NOTE: The name was escaped because it collided with a Scala keyword. */
    val `type`: Rep[String] = column[String]("type", O.Length(20,varying=true))
    /** Database column script SqlType(varchar), Length(1000,true) */
    val script: Rep[String] = column[String]("script", O.Length(1000,varying=true))
    /** Database column checksum SqlType(int4), Default(None) */
    val checksum: Rep[Option[Int]] = column[Option[Int]]("checksum", O.Default(None))
    /** Database column installed_by SqlType(varchar), Length(100,true) */
    val installedBy: Rep[String] = column[String]("installed_by", O.Length(100,varying=true))
    /** Database column installed_on SqlType(timestamp) */
    val installedOn: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("installed_on")
    /** Database column execution_time SqlType(int4) */
    val executionTime: Rep[Int] = column[Int]("execution_time")
    /** Database column success SqlType(bool) */
    val success: Rep[Boolean] = column[Boolean]("success")

    /** Index over (installedRank) (database name schema_version_ir_idx) */
    val index1 = index("schema_version_ir_idx", installedRank)
    /** Index over (success) (database name schema_version_s_idx) */
    val index2 = index("schema_version_s_idx", success)
    /** Index over (versionRank) (database name schema_version_vr_idx) */
    val index3 = index("schema_version_vr_idx", versionRank)
  }
  /** Collection-like TableQuery object for table SchemaVersion */
  lazy val SchemaVersion = new TableQuery(tag => new SchemaVersion(tag))

  /** Entity class storing rows of table Session
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param email Database column email SqlType(varchar), Length(255,true)
   *  @param token Database column token SqlType(text)
   *  @param idpentityid Database column idpentityid SqlType(varchar), Length(255,true) */
  case class SessionRow(id: Int, email: String, token: String, idpentityid: String)
  /** GetResult implicit for fetching SessionRow objects using plain SQL queries */
  implicit def GetResultSessionRow(implicit e0: GR[Int], e1: GR[String]): GR[SessionRow] = GR{
    prs => import prs._
    SessionRow.tupled((<<[Int], <<[String], <<[String], <<[String]))
  }
  /** Table description of table session. Objects of this class serve as prototypes for rows in queries. */
  class Session(_tableTag: Tag) extends Table[SessionRow](_tableTag, "session") {
    def * = (id, email, token, idpentityid) <> (SessionRow.tupled, SessionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(email), Rep.Some(token), Rep.Some(idpentityid)).shaped.<>({r=>import r._; _1.map(_=> SessionRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column email SqlType(varchar), Length(255,true) */
    val email: Rep[String] = column[String]("email", O.Length(255,varying=true))
    /** Database column token SqlType(text) */
    val token: Rep[String] = column[String]("token")
    /** Database column idpentityid SqlType(varchar), Length(255,true) */
    val idpentityid: Rep[String] = column[String]("idpentityid", O.Length(255,varying=true))

    /** Uniqueness Index over (email) (database name session_email) */
    val index1 = index("session_email", email, unique=true)
    /** Index over (email) (database name session_email_idx) */
    val index2 = index("session_email_idx", email)
  }
  /** Collection-like TableQuery object for table Session */
  lazy val Session = new TableQuery(tag => new Session(tag))

  /** Entity class storing rows of table Synchronization
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param created Database column created SqlType(timestamp)
   *  @param henkiloOid Database column henkilo_oid SqlType(varchar), Length(255,true)
   *  @param hakuOid Database column haku_oid SqlType(varchar), Length(255,true)
   *  @param hakukohdeOid Database column hakukohde_oid SqlType(varchar), Length(255,true)
   *  @param status Database column status SqlType(varchar), Length(255,true)
   *  @param updated Database column updated SqlType(timestamp), Default(None) */
  case class SynchronizationRow(id: Int, created: java.sql.Timestamp, henkiloOid: String, hakuOid: String, hakukohdeOid: String, status: String, updated: Option[java.sql.Timestamp] = None)
  /** GetResult implicit for fetching SynchronizationRow objects using plain SQL queries */
  implicit def GetResultSynchronizationRow(implicit e0: GR[Int], e1: GR[java.sql.Timestamp], e2: GR[String], e3: GR[Option[java.sql.Timestamp]]): GR[SynchronizationRow] = GR{
    prs => import prs._
    SynchronizationRow.tupled((<<[Int], <<[java.sql.Timestamp], <<[String], <<[String], <<[String], <<[String], <<?[java.sql.Timestamp]))
  }
  /** Table description of table synchronization. Objects of this class serve as prototypes for rows in queries. */
  class Synchronization(_tableTag: Tag) extends Table[SynchronizationRow](_tableTag, "synchronization") {
    def * = (id, created, henkiloOid, hakuOid, hakukohdeOid, status, updated) <> (SynchronizationRow.tupled, SynchronizationRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(created), Rep.Some(henkiloOid), Rep.Some(hakuOid), Rep.Some(hakukohdeOid), Rep.Some(status), updated).shaped.<>({r=>import r._; _1.map(_=> SynchronizationRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column created SqlType(timestamp) */
    val created: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("created")
    /** Database column henkilo_oid SqlType(varchar), Length(255,true) */
    val henkiloOid: Rep[String] = column[String]("henkilo_oid", O.Length(255,varying=true))
    /** Database column haku_oid SqlType(varchar), Length(255,true) */
    val hakuOid: Rep[String] = column[String]("haku_oid", O.Length(255,varying=true))
    /** Database column hakukohde_oid SqlType(varchar), Length(255,true) */
    val hakukohdeOid: Rep[String] = column[String]("hakukohde_oid", O.Length(255,varying=true))
    /** Database column status SqlType(varchar), Length(255,true) */
    val status: Rep[String] = column[String]("status", O.Length(255,varying=true))
    /** Database column updated SqlType(timestamp), Default(None) */
    val updated: Rep[Option[java.sql.Timestamp]] = column[Option[java.sql.Timestamp]]("updated", O.Default(None))

    /** Foreign key referencing User (database name synchronization_henkilo_oid_fkey) */
    lazy val userFk = foreignKey("synchronization_henkilo_oid_fkey", Rep.Some(henkiloOid), User)(r => r.henkiloOid, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Synchronization */
  lazy val Synchronization = new TableQuery(tag => new Synchronization(tag))

  /** Entity class storing rows of table User
   *  @param id Database column id SqlType(serial), AutoInc, PrimaryKey
   *  @param henkiloOid Database column henkilo_oid SqlType(varchar), Length(255,true), Default(None)
   *  @param email Database column email SqlType(varchar), Length(255,true)
   *  @param idpentityid Database column idpentityid SqlType(varchar), Length(255,true)
   *  @param firstname Database column firstname SqlType(varchar), Length(255,true)
   *  @param lastname Database column lastname SqlType(varchar), Length(255,true)
   *  @param gender Database column gender SqlType(varchar), Length(255,true)
   *  @param birthdate Database column birthdate SqlType(date)
   *  @param personid Database column personid SqlType(varchar), Length(255,true), Default(None)
   *  @param nativeLanguage Database column native_language SqlType(varchar), Length(255,true)
   *  @param nationality Database column nationality SqlType(varchar), Length(255,true) */
  case class UserRow(id: Int, henkiloOid: Option[String] = None, email: String, idpentityid: String, firstname: String, lastname: String, gender: String, birthdate: java.sql.Date, personid: Option[String] = None, nativeLanguage: String, nationality: String)
  /** GetResult implicit for fetching UserRow objects using plain SQL queries */
  implicit def GetResultUserRow(implicit e0: GR[Int], e1: GR[Option[String]], e2: GR[String], e3: GR[java.sql.Date]): GR[UserRow] = GR{
    prs => import prs._
    UserRow.tupled((<<[Int], <<?[String], <<[String], <<[String], <<[String], <<[String], <<[String], <<[java.sql.Date], <<?[String], <<[String], <<[String]))
  }
  /** Table description of table user. Objects of this class serve as prototypes for rows in queries. */
  class User(_tableTag: Tag) extends Table[UserRow](_tableTag, "user") {
    def * = (id, henkiloOid, email, idpentityid, firstname, lastname, gender, birthdate, personid, nativeLanguage, nationality) <> (UserRow.tupled, UserRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), henkiloOid, Rep.Some(email), Rep.Some(idpentityid), Rep.Some(firstname), Rep.Some(lastname), Rep.Some(gender), Rep.Some(birthdate), personid, Rep.Some(nativeLanguage), Rep.Some(nationality)).shaped.<>({r=>import r._; _1.map(_=> UserRow.tupled((_1.get, _2, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9, _10.get, _11.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(serial), AutoInc, PrimaryKey */
    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    /** Database column henkilo_oid SqlType(varchar), Length(255,true), Default(None) */
    val henkiloOid: Rep[Option[String]] = column[Option[String]]("henkilo_oid", O.Length(255,varying=true), O.Default(None))
    /** Database column email SqlType(varchar), Length(255,true) */
    val email: Rep[String] = column[String]("email", O.Length(255,varying=true))
    /** Database column idpentityid SqlType(varchar), Length(255,true) */
    val idpentityid: Rep[String] = column[String]("idpentityid", O.Length(255,varying=true))
    /** Database column firstname SqlType(varchar), Length(255,true) */
    val firstname: Rep[String] = column[String]("firstname", O.Length(255,varying=true))
    /** Database column lastname SqlType(varchar), Length(255,true) */
    val lastname: Rep[String] = column[String]("lastname", O.Length(255,varying=true))
    /** Database column gender SqlType(varchar), Length(255,true) */
    val gender: Rep[String] = column[String]("gender", O.Length(255,varying=true))
    /** Database column birthdate SqlType(date) */
    val birthdate: Rep[java.sql.Date] = column[java.sql.Date]("birthdate")
    /** Database column personid SqlType(varchar), Length(255,true), Default(None) */
    val personid: Rep[Option[String]] = column[Option[String]]("personid", O.Length(255,varying=true), O.Default(None))
    /** Database column native_language SqlType(varchar), Length(255,true) */
    val nativeLanguage: Rep[String] = column[String]("native_language", O.Length(255,varying=true))
    /** Database column nationality SqlType(varchar), Length(255,true) */
    val nationality: Rep[String] = column[String]("nationality", O.Length(255,varying=true))

    /** Uniqueness Index over (henkiloOid) (database name henkilo_oid) */
    val index1 = index("henkilo_oid", henkiloOid, unique=true)
    /** Uniqueness Index over (email) (database name user_email) */
    val index2 = index("user_email", email, unique=true)
    /** Index over (email) (database name user_email_idx) */
    val index3 = index("user_email_idx", email)
  }
  /** Collection-like TableQuery object for table User */
  lazy val User = new TableQuery(tag => new User(tag))
}
