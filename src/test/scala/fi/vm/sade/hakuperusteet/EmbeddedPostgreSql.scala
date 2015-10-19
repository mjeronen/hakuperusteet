package fi.vm.sade.hakuperusteet

import java.nio.charset.StandardCharsets
import java.nio.file.{StandardOpenOption, Files}

import ru.yandex.qatools.embed.postgresql.PostgresStarter
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig

object EmbeddedPostgreSql {

  lazy val config = PostgresConfig.defaultWithDbName("test", "oph", "test")
  lazy val process = PostgresStarter.getDefaultInstance().prepare(config)


  def startEmbeddedPostgreSql = {
    writeConfigFile
    process.start()
  }

  def writeConfigFile = {
    Files.write(Configuration.conffile.toPath, configAsString.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE)
  }

  def dbUrl = s"jdbc:postgresql://${config.net().host()}:${config.net().port()}/${config.storage().dbName()}"
  def user = s"${config.credentials().username()}"
  def password = s"${config.credentials().password()}"

  private def configAsString = s"""
         |hakuperusteet.db.url="${dbUrl}"
         |hakuperusteet.db.username="${user}"
         |hakuperusteet.db.password="${password}"
       """.stripMargin

}
