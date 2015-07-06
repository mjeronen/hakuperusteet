package fi.vm.sade.hakuperusteet

import org.json4s.NoTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import org.scalatra._

case class Status(msg: String)

class StatusServlet extends ScalatraServlet {

  get("/") {
    implicit val formats = Serialization.formats(NoTypeHints)
    write(Status("Ok"))
  }
}
