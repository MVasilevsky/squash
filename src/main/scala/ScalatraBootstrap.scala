import javax.servlet.ServletContext

import mvas.squash._
import mvas.squash.db.DatabaseInit
import org.scalatra._
import org.squeryl.PrimitiveTypeMode._

class ScalatraBootstrap extends LifeCycle with DatabaseInit {

  override def init(context: ServletContext) {
    configureDb()

    transaction {
      if (Model.findTablesFor(Model.quotes).isEmpty) {
        Model.create
        Model.createInitialData()
        println("Created the schema")
      } else {
        println("Found the schema")
      }
    }

    context.mount(new SquashServlet, "/*")
  }

  override def destroy(context: ServletContext) {
    closeDbConnection()
  }

}
