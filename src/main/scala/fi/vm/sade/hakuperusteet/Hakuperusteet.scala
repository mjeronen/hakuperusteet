package fi.vm.sade.hakuperusteet

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener


object Hakuperusteet {
  def main(args: Array[String]) {
    val port = if(System.getenv("PORT") != null) System.getenv("PORT").toInt else 8080
    val server = new Server(port)
    val context = new WebAppContext()
    context setContextPath ("/hakuperusteet/")
    context.setResourceBase("src/main/webapp")
    context.addEventListener(new ScalatraListener)
    context.addServlet(classOf[DefaultServlet], "/hakuperusteet/")

    server.setHandler(context)

    server.start
    server.join
  }
}
