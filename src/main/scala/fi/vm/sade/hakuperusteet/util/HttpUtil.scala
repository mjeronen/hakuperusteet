package fi.vm.sade.hakuperusteet.util

import java.net.URL

object HttpUtil {

  def urlToString(url: String) = io.Source.fromInputStream(new URL(url).openStream()).mkString
}
