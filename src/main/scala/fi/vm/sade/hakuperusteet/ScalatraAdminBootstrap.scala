package fi.vm.sade.hakuperusteet

import java.util.EnumSet
import javax.servlet.{DispatcherType, ServletContext}

import fi.vm.sade.hakuperusteet.admin.AdminServlet
import fi.vm.sade.hakuperusteet.db.GlobalExecutionContext
import org.scalatra.LifeCycle

class ScalatraAdminBootstrap extends LifeCycle with GlobalExecutionContext {
  val config = Configuration.props

  override def init(context: ServletContext) {

    //context.addFilter("cas", new CasFilter(config))
    //.addMappingForUrlPatterns(EnumSet.allOf(classOf[DispatcherType]), true, "/*")

    context mount(new AdminServlet(config), "/")
  }

}
