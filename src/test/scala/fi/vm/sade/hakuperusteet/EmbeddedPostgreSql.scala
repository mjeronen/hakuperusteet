package fi.vm.sade.hakuperusteet

import ru.yandex.qatools.embed.postgresql.PostgresStarter
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig

object EmbeddedPostgreSql {

  private val config = PostgresConfig.defaultWithDbName("test", "oph", "test")
  private val process = PostgresStarter.getDefaultInstance().prepare(config)
  private val started = startEmbeddedPostgreSql

  def ensureDatabaseStarted() = {
    started
  }

  private def startEmbeddedPostgreSql = {
    process.start()
    sys addShutdownHook {
      process.stop()
    }
    true
  }

  private def dbUrl = s"jdbc:postgresql://${config.net().host()}:${config.net().port()}/${config.storage().dbName()}"
  private def user = s"${config.credentials().username()}"
  private def password = s"${config.credentials().password()}"
  def configAsMap = Map("hakuperusteet.db.url"-> dbUrl, "hakuperusteet.db.user" -> user, "hakuperusteet.db.password" -> password)
}
