package fi.vm.sade.hakuperusteet

import org.scalatra._


class StatusServlet extends ScalatraServlet {

  get("/") {
    "OK"
  }
}
