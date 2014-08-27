package mvas.squash.db

import java.security.MessageDigest

/**
 * Created by Max on 27.08.2014.
 */
object Hasher {

  def hash(source: String) = {
    val md = MessageDigest.getInstance("SHA-256")
    md.update(source.getBytes("UTF-8"))
    val digest = md.digest()
    new String(digest)
  }

}
