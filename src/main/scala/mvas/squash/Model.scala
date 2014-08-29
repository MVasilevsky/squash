package mvas.squash

import java.sql.Timestamp
import java.util.Date

import org.squeryl.PrimitiveTypeMode._

import org.squeryl.{Schema, KeyedEntity}

/**
 * Created by Max on 24.08.2014.
 */
abstract class Model

class BaseEntity extends KeyedEntity[Int] {
  var id: Int = 0
  var lastModified = new Timestamp(System.currentTimeMillis)
}

class User(val login: String, var pass: String) extends BaseEntity {

  def this(id: Int, login: String, pass: String) = {
    this(login, pass)
    this.id = id
  }

}

class Quote(val picture: String, val uploader: User, val date: Date, var rating: Double, var tags: List[Tag]) extends BaseEntity {
  def this(id: Int, picture: String, uploader: User, date: Date, rating: Double, tags: List[Tag]) {
    this(picture, uploader, date, rating, tags)
    this.id = id
  }
}

class Tag(val name: String) extends BaseEntity {
  def this(id: Int, name:String) {
    this(name)
    this.id = id
  }
}

class Mark(val quote: Quote, val points: Int, val user: User, val date: Date) extends BaseEntity {
  def this(id: Int, quote: Quote, points: Int, user: User, date: Date) {
    this(quote, points, user, date)
    this.id = id
  }
}

object Model extends Schema {
  val users = table[User]
  val quotes = table[Quote]
  val tags = table[Tag]
  val marks = table[Mark]

  on(users)(u => declare(
    u.id is autoIncremented
  ))

  on(quotes)(q => declare(
    q.id is autoIncremented
  ))

  on(tags)(t => declare(
    t.id is autoIncremented
  ))

  on(marks)(m => declare(
    m.id is autoIncremented
  ))

  def createInitialData() {
    tags.insert(new Tag(1, "Groushee"))
    tags.insert(new Tag(2, "Sleevy"))
    tags.insert(new Tag(3, "Mini kolby"))

    users.insert(new User(1, "mvas", "e63807f81d6c4929d3692acc590f4ea30f9ab543c820c145648ef1c14477da51"))
    users.insert(new User(2, "dab", "e63807f81d6c4929d3692acc590f4ea30f9ab543c820c145648ef1c14477da51"))
    users.insert(new User(3, "rsd", "e63807f81d6c4929d3692acc590f4ea30f9ab543c820c145648ef1c14477da51"))
  }

}