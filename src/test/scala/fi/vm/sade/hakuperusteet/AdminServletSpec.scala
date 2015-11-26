package fi.vm.sade.hakuperusteet

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import fi.vm.sade.hakuperusteet.admin.AdminServlet
import fi.vm.sade.hakuperusteet.admin.auth.CasBasicAuthStrategy
import fi.vm.sade.hakuperusteet.domain.{CasSession, ApplicationAndUser}
import fi.vm.sade.hakuperusteet.oppijantunnistus.OppijanTunnistus
import fi.vm.sade.hakuperusteet.swagger.AdminSwagger
import fi.vm.sade.hakuperusteet.validation.{ApplicationObjectValidator, UserValidator}
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatra.test.scalatest.ScalatraSuite
import org.json4s.native.Serialization.{read, write}

@RunWith(classOf[JUnitRunner])
class AdminServletSpec extends FunSuite with ScalatraSuite with ServletTestDependencies {
  implicit val swagger = new AdminSwagger
  val stream = getClass.getResourceAsStream("/logoutRequest.xml")
  val logoutRequest = scala.io.Source.fromInputStream( stream ).mkString

  val s = new AdminServlet("/webapp-admin/index.html",config,OppijanTunnistus(config), UserValidator(countries,languages), ApplicationObjectValidator(countries,educations), database) {
    override protected def registerAuthStrategies = {
      scentry.register("CAS", app => new CasBasicAuthStrategy(app, cfg) {
        override def authenticate()(implicit request: HttpServletRequest, response: HttpServletResponse): Option[CasSession] = {
          Some(CasSession(None, "oid", "username", List("APP_HAKUPERUSTEETADMIN_CRUD"), "ticket"))
        }
      })
    }
  }

  addServlet(s, "/*")

  test("CAS logout") {
    post("/", Map("logoutRequest"->logoutRequest)) {
      status should equal (200)
    }
  }

}
