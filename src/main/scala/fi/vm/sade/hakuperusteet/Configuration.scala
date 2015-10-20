package fi.vm.sade.hakuperusteet

import java.io.File

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.util.Try
import com.typesafe.config._

object Configuration extends LazyLogging {

  val props = ConfigFactory
    .parseFile(new File(sys.props.getOrElse("hakuperusteet.properties","")))
    .withFallback(ConfigFactory.parseResources("reference.conf"))
    .resolve
}
