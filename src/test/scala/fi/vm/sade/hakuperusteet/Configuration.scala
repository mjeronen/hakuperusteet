package fi.vm.sade.hakuperusteet

import java.io.File

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

/*
 * This file is used during tests (sbt/scala feature/hack/kludge) and it overriders actual Configuration.scala
 */
object Configuration extends LazyLogging {
  logger.info("Using Configuration from test classpath!")

  private val useHsqldb = System.getProperty("useHsql", "false") == "true"

  if(useHsqldb) {
    logger.info("Using hsqldb configuration")
  }

  val props = ConfigFactory
    .parseFile(new File(""))
    .withFallback(if(useHsqldb) ConfigFactory.parseResources("hsqlReference.conf") else ConfigFactory.empty())
    .withFallback(ConfigFactory.parseResources("reference.conf"))
    .resolve
}
