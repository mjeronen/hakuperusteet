package fi.vm.sade.hakuperusteet.db

import fi.vm.sade.hakuperusteet.Configuration

object CodeGenerator extends App with GlobalExecutionContext {
  private val config = Configuration.props
  HakuperusteetDatabase.initDatabase()

  slick.codegen.SourceCodeGenerator.main(Array(
    "slick.driver.PostgresDriver",
    "org.postgresql.Driver", config.getString("hakuperusteet.db.url"), "src/main/scala", "fi.vm.sade.hakuperusteet.db.generated", config.getString("hakuperusteet.db.username"), config.getString("hakuperusteet.db.password"))
  )
}
