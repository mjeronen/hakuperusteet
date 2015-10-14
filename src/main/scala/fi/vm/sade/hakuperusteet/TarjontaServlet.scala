package fi.vm.sade.hakuperusteet

import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.tarjonta.Tarjonta
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.ScalatraServlet
import org.scalatra.json.NativeJsonSupport

import scala.util.{Failure, Success, Try}

class TarjontaServlet(tarjonta: Tarjonta) extends ScalatraServlet with NativeJsonSupport with LazyLogging {
  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  get("/:hakukohdeoid") {
    Try { tarjonta.getApplicationObject(params("hakukohdeoid")) } match {
      case Success(as) => as
      case Failure(f) =>
        logger.error("TarjontaServlet throws", f)
        halt(500)
    }
  }

  error { case e: Throwable => logger.error("uncaught exception", e) }
}
