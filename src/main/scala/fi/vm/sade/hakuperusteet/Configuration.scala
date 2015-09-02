package fi.vm.sade.hakuperusteet

import java.io.File

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.slf4j.LazyLogging

import scala.util.Try
import com.typesafe.config._

object Configuration extends LazyLogging {
  private val isMockConfig = Option(System.getProperty("mock"))
    .exists(m => Try(m.toBoolean).toOption.exists(_.equals(true)))

  if(isMockConfig) {
    logger.info("Using mock configuration")
  }

  val props = ConfigFactory
    .parseFile(new File(sys.props.getOrElse("hakuperusteet.properties","")))
    .withFallback(if(isMockConfig) ConfigFactory.parseResources("mockReference.conf") else ConfigFactory.empty())
    .withFallback(ConfigFactory.parseResources("reference.conf"))
    .resolve

}
