package fi.vm.sade.hakuperusteet

import java.io.File
import java.util.{Properties, EnumSet}
import javax.servlet.{DispatcherType, ServletContext}

import fi.vm.sade.hakuperusteet.admin.AdminServlet
import fi.vm.sade.hakuperusteet.db.{HakuperusteetDatabase, GlobalExecutionContext}
import fi.vm.sade.hakuperusteet.koodisto.Koodisto
import org.scalatra.{ScalatraServlet, LifeCycle}

import scala.io.Source

class ScalatraAdminBootstrap extends LifeCycle with GlobalExecutionContext {
  val config = Configuration.props
  val database = HakuperusteetDatabase.init(config)
  val countries = Koodisto.initCountries(config)
  val languages = Koodisto.initLanguages(config)
  val educations = Koodisto.initBaseEducation(config)

  override def init(context: ServletContext) {
    context mount(new PropertiesServlet(config, countries, languages, educations), "/api/v1/properties")
    context mount(new AdminServlet("/webapp-admin/index.html",config, database), "/")
  }

}
