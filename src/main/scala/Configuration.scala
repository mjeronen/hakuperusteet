package fi.vm.sade.hakuperusteet

import java.io.File

import com.typesafe.config.ConfigFactory

object Configuration {
  def props = ConfigFactory
    .parseFile(new File(sys.props.getOrElse("hakuperusteet.properties","")))
    .withFallback(ConfigFactory.parseResources("reference.conf"))
    .resolve

}
