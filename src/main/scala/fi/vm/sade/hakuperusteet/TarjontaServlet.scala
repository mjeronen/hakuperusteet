package fi.vm.sade.hakuperusteet

import fi.vm.sade.hakuperusteet.tarjonta.Tarjonta
import org.json4s.{DefaultFormats, Formats, NoTypeHints}
import org.scalatra.ScalatraServlet
import org.json4s.native.JsonMethods._
import org.json4s.JsonDSL._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import org.scalatra.json.NativeJsonSupport

class TarjontaServlet(tarjonta: Tarjonta) extends ScalatraServlet with NativeJsonSupport {
  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  get("/:hakukohdeoid") {
    tarjonta.getApplicationObject(params("hakukohdeoid"))
  }

}
