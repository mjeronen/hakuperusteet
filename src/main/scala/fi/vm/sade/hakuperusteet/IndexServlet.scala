package fi.vm.sade.hakuperusteet

import com.typesafe.scalalogging.LazyLogging
import org.scalatra.ScalatraServlet

import scala.io.Source

class IndexServlet extends ScalatraServlet with LazyLogging {
  val indexHtml = Source.fromURL(getClass.getResource("/webapp/index.html")).mkString

  before() {
    contentType = "text/html"
  }

  get("/*") {
    indexHtml
  }
}
