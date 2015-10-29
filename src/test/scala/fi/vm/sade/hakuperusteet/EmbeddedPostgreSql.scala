package fi.vm.sade.hakuperusteet

import java.nio.charset.StandardCharsets
import java.nio.file.{StandardOpenOption, Files}

import ru.yandex.qatools.embed.postgresql.PostgresStarter
import ru.yandex.qatools.embed.postgresql.config.{AbstractPostgresConfig, PostgresConfig}
import ru.yandex.qatools.embed.postgresql.distribution.Version.Main._

object EmbeddedPostgreSql {

  lazy val config = new PostgresConfig(PRODUCTION, new AbstractPostgresConfig.Net, new AbstractPostgresConfig.Storage("test"), new AbstractPostgresConfig.Timeout(45000), new AbstractPostgresConfig.Credentials("oph", "test"))
  lazy val process = PostgresStarter.getDefaultInstance().prepare(config)

  def startEmbeddedPostgreSql = {
    process.start()
  }

  def dbUrl = s"jdbc:postgresql://${config.net().host()}:${config.net().port()}/${config.storage().dbName()}"
  def user = s"${config.credentials().username()}"
  def password = s"${config.credentials().password()}"

  def configAsMap = Map("hakuperusteet.db.url"-> dbUrl, "hakuperusteet.db.user" -> user, "hakuperusteet.db.password" -> password)
}
