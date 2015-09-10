import javax.servlet.ServletContext

import fi.vm.sade.hakuperusteet._
import fi.vm.sade.hakuperusteet.db.{GlobalExecutionContext, HakuperusteetDatabase}
import fi.vm.sade.hakuperusteet.koodisto.{Koodisto, Languages, Countries}
import fi.vm.sade.hakuperusteet.rsa.RSASigner
import fi.vm.sade.hakuperusteet.tarjonta.Tarjonta
import org.scalatra.LifeCycle

class ScalatraBootstrap extends LifeCycle with GlobalExecutionContext {
  val config = Configuration.props
  val database = HakuperusteetDatabase.init(config)
  val signer = RSASigner.init(config)
  val countries = Koodisto.initCountries(config)
  val languages = Koodisto.initLanguages(config)
  val educations = Koodisto.initBaseEducation(config)
  val tarjonta = Tarjonta.init(config)

  override def init(context: ServletContext) {
    context mount(new StatusServlet, "/api/v1/status")
    context mount(new TestServlet(config), "/api/v1/test")
    context mount(new VetumaServlet(config, database), "/api/v1/vetuma")
    context mount(new TarjontaServlet(tarjonta), "/api/v1/tarjonta")
    context mount(new PropertiesServlet(config, countries, languages, educations), "/api/v1/properties")
    context mount(new SessionServlet(config, database, countries), "/api/v1/session")
    context mount(new FormRedirectServlet(config, database, signer, countries), "/api/v1/form")
  }
}
