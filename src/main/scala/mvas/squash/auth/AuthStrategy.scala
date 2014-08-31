package mvas.squash.auth

/**
 * Created by Max on 30.08.2014.
 */

import java.util.logging.Logger
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import mvas.squash.db.Hasher
import mvas.squash.Model
import org.scalatra.ScalatraBase
import org.scalatra.auth.{ScentrySupport, ScentryConfig}
import org.scalatra.auth.strategy.{BasicAuthSupport, BasicAuthStrategy}
import org.squeryl.PrimitiveTypeMode._


class SquashAuthStrategy(protected override val app: ScalatraBase, realm: String)
  extends BasicAuthStrategy[AuthUser](app, realm) {

  protected def validate(userName: String, password: String) (implicit request: HttpServletRequest, response: HttpServletResponse): Option[AuthUser] = {
    val found = from(Model.users)(u => where(u.login === userName) select u)
    val usr = if (found.isEmpty) return None else found.single
    if (Hasher.hash(password).equalsIgnoreCase(usr.pass)) {
      val user = new AuthUser(usr.id.toString)
      Some(user)
    } else {
      None
    }
  }

  override protected def getUserId(user: AuthUser)(implicit request: HttpServletRequest, response: HttpServletResponse): String = user.id
}

case class AuthUser(id: String)

trait AuthenticationSupport extends ScentrySupport[AuthUser] with BasicAuthSupport[AuthUser] {
  self: ScalatraBase =>

  val realm = "YOU VESELEESHS'A?"

  protected def fromSession = { case id: String => AuthUser(id) }
  protected def toSession   = { case usr: AuthUser => usr.id }

  protected val scentryConfig = new ScentryConfig {}.asInstanceOf[ScentryConfiguration]


  override protected def configureScentry() = {
    scentry.unauthenticated {
      scentry.strategies("Basic").unauthenticated()
    }
  }

  override protected def registerAuthStrategies() = {
    scentry.register("Basic", app => new SquashAuthStrategy(app, realm))
  }

}