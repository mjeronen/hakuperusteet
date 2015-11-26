package fi.vm.sade.hakuperusteet.util

import org.json4s._
import org.json4s.jackson.JsonMethods._

object Translate {
  val strings = loadStrings

  def apply(key: String, lang: String) = strings(key + "." + lang)

  def flattenKeysAndValues(json: Map[String, Any]): Map[String, Any] = {
    json.flatMap { case (k, v) => v match {
      case json1: Map[String, Any] =>
        flattenKeysAndValues(json1).map { case (fk, fv) => (k + "." + fk, fv) }.toList
      case _ =>
        List((k, v))
    }
    }
  }

  def loadStrings: Map[String, String] = {
    implicit val formats = org.json4s.DefaultFormats
    val file = io.Source.fromInputStream(getClass.getResourceAsStream("/web-translations.json")).mkString
    val map: Map[String, Any] = parse(file).extract[Map[String, Any]]
    val flattenedMap: Map[String, Any] = flattenKeysAndValues(map)
    flattenedMap.map { case (k, v) => (k, v.toString) }
  }
}
