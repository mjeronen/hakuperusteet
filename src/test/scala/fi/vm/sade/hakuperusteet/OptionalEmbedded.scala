package fi.vm.sade.hakuperusteet

import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.util.ConfigUtil

object OptionalEmbeddedDB {
  private val embeddedDB = initEmbedded
  private def initEmbedded = {
    if(HakuperusteetTestServer.isEmbeddedConfig) {
      ConfigUtil.writeConfigFile(EmbeddedPostgreSql.configAsMap)
      EmbeddedPostgreSql.ensureDatabaseStarted()
      val config = Configuration.props
      HakuperusteetDatabase.init(config)
    }
    true
  }
  def ensureEmbeddedIsStartedIfNeeded() = embeddedDB
}

trait OptionalEmbeddedDB {
  OptionalEmbeddedDB.ensureEmbeddedIsStartedIfNeeded()
}