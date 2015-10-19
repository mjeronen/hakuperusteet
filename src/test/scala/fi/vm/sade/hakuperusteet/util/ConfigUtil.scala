package fi.vm.sade.hakuperusteet.util

import java.nio.charset.StandardCharsets
import java.nio.file.{StandardOpenOption, Files}

import fi.vm.sade.hakuperusteet.Configuration

object ConfigUtil {

  def mapToConfigString(m: Map[String,Any]) = m.map{case (key,value)=> s"""${key}="${value}""""} mkString "\n"

  def writeConfigFile(m: Map[String,Any]) = {
    Files.write(Configuration.conffile.toPath, mapToConfigString(m).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE)
  }
}
