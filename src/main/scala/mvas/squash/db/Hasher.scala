package mvas.squash.db

import java.security.MessageDigest

/**
 * Created by Max on 27.08.2014.
 */
object Hasher {

  def byteArrayToHex(buf: Array[Byte]): String = buf.map("%02X" format _).mkString

  def hash(source: String) = {
    val md = MessageDigest.getInstance("SHA-256")
    md.update(source.getBytes("UTF-8"))
    byteArrayToHex(md.digest())
  }

}
