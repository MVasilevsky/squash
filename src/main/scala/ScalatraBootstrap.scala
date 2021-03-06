import javax.servlet.ServletContext

import mvas.squash._
import mvas.squash.db.DatabaseInit
import org.scalatra._
import org.scalatra.servlet.MultipartConfig
import org.squeryl.PrimitiveTypeMode._

class ScalatraBootstrap extends LifeCycle with DatabaseInit {

  override def init(context: ServletContext) {
    configureDb()

    transaction {
      Model.create
      Model.createInitialData()
      println("Created the schema")
    }

    context.mount(new SquashServlet, "/*")
//    context.initParameters("org.scalatra.Port") = "443"
//    context.initParameters("org.scalatra.ForceHttps") = "true"
  }

  override def destroy(context: ServletContext) {
    closeDbConnection()
  }

}
