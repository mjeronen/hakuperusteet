package fi.vm.sade.hakuperusteet.db

import java.util.Date

import fi.vm.sade.hakuperusteet.util.ConfigUtil
import fi.vm.sade.hakuperusteet.{EmbeddedPostgreSql, Configuration}
import fi.vm.sade.hakuperusteet.domain.{User, PaymentStatus, Payment}
import org.flywaydb.core.Flyway
import org.scalatest.{BeforeAndAfterEach, BeforeAndAfterAll, Matchers, FlatSpec}

class HakuperusteetDatabaseSpec extends FlatSpec with Matchers with BeforeAndAfterAll with GlobalExecutionContext {
  behavior of "HakuperusteetDatabase"

  ConfigUtil.writeConfigFile(EmbeddedPostgreSql.configAsMap)
  EmbeddedPostgreSql.startEmbeddedPostgreSql

  val config = Configuration.props
  val db = HakuperusteetDatabase.init(config)

  override def beforeAll() = {
    clearDatabase()
  }

  private def clearDatabase() {
    val url = config.getString("hakuperusteet.db.url")
    val user = config.getString("hakuperusteet.db.username")
    val password = config.getString("hakuperusteet.db.password")
    val flyway = new Flyway
    flyway.setLocations("filesystem:src/main/resources/db/migration")
    flyway.setDataSource(url, user, password)
    flyway.setSchemas(HakuperusteetDatabase.schemaName)
    flyway.setValidateOnMigrate(false)
    flyway.clean
    flyway.migrate
  }

  it should "should create new session" in {
    val user = new User(None, Some("personOid.1.1.1"), "", "", "", new Date(), None, "", "", "", "")
    db.upsertUser(user)

    db.findPayments(user).length shouldEqual 0
    val p = Payment(None, "personOid.1.1.1", new Date(), "refNo", "orderNo", "paymCallId", PaymentStatus.ok)
    db.upsertPayment(p)
    db.findPayments(user).length shouldEqual 1
  }
}
