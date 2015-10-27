package fi.vm.sade.hakuperusteet.db

import fi.vm.sade.hakuperusteet.Configuration

object CodeGenerator extends App {
  private val config = Configuration.props
  HakuperusteetDatabase.init(config, GlobalExecutionContext.asyncExecutor)

  slick.codegen.SourceCodeGenerator.main(Array(
    "slick.driver.PostgresDriver",
    "org.postgresql.Driver", config.getString("hakuperusteet.db.url"), "src/main/scala", "fi.vm.sade.hakuperusteet.db.generated", config.getString("hakuperusteet.db.username"), config.getString("hakuperusteet.db.password"))
  )
}
