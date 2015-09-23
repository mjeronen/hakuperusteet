package fi.vm.sade.hakuperusteet

import com.typesafe.scalalogging.LazyLogging
import fi.vm.sade.hakuperusteet.tarjonta.Tarjonta
import org.json4s.{DefaultFormats, Formats}
import org.scalatra.ScalatraServlet
import org.scalatra.json.NativeJsonSupport

class TarjontaServlet(tarjonta: Tarjonta) extends ScalatraServlet with NativeJsonSupport with LazyLogging {
  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  get("/:hakukohdeoid") {
    tarjonta.getApplicationObject(params("hakukohdeoid"))
  }

  error { case e: Throwable => logger.error("uncaught exception", e) }
}
