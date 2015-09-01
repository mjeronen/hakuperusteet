package fi.vm.sade.hakuperusteet

import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.LazyLogging
import fi.vm.sade.hakuperusteet.db.HakuperusteetDatabase
import fi.vm.sade.hakuperusteet.vetuma.{Vetuma, VetumaUrl}
import org.joda.time.DateTime
import org.scalatra.ScalatraServlet

class VetumaServlet(config: Config, db: HakuperusteetDatabase) extends HakuperusteetServlet(config, db) with LazyLogging {

  before() {
    contentType = "application/json"
  }

  get("/openvetuma") {
    failUnlessAuthenticated

    val language = "fi"
    val ref = "1234561"
    val orderNro = Vetuma.generateOrderNumber

    val v = VetumaUrl(
      config.getString("vetuma.host"),
      DateTime.now,
      language,
      config.getString("vetuma.success.url"),
      config.getString("vetuma.cancel.url"),
      config.getString("vetuma.error.url"),
      config.getString("vetuma.app.name"),
      config.getString("vetuma.amount"),
      ref,
      orderNro,
      config.getString("vetuma.msg.buyer"),
      config.getString("vetuma.msg.seller"),
      config.getString("vetuma.msg.form")
    )
    v.toUrl
  }

  post("/return/ok") {
    val url = config.getString("host.url.base") + "?result=ok"
    halt(status = 303, headers = Map("Location" -> url.toString))
  }

  post("/return/cancel") {
    val url = config.getString("host.url.base") + "?result=cancel"
    halt(status = 303, headers = Map("Location" -> url))
  }

  post("/return/error") {
    val url = config.getString("host.url.base") + "?result=error"
    halt(status = 303, headers = Map("Location" -> url))
  }
}
