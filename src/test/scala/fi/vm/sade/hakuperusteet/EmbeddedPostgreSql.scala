package fi.vm.sade.hakuperusteet

import java.nio.charset.StandardCharsets
import java.nio.file.{StandardOpenOption, Files}

import ru.yandex.qatools.embed.postgresql.PostgresStarter
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig

object EmbeddedPostgreSql {

  lazy val config = PostgresConfig.defaultWithDbName("test", "oph", "test")
  lazy val process = PostgresStarter.getDefaultInstance().prepare(config)

  def startEmbeddedPostgreSql = {
    process.start()
  }

  def dbUrl = s"jdbc:postgresql://${config.net().host()}:${config.net().port()}/${config.storage().dbName()}"
  def user = s"${config.credentials().username()}"
  def password = s"${config.credentials().password()}"

  def configAsMap = Map("hakuperusteet.db.url"-> dbUrl, "hakuperusteet.db.user" -> user, "hakuperusteet.db.password" -> password)
}
