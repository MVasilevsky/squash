package mvas.squash

import java.util.Date

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.ManyToOne

import org.squeryl.{Schema, KeyedEntity}

/**
 * Created by Max on 24.08.2014.
 */
abstract class Model

class BaseEntity extends KeyedEntity[Int]{
  var id: Int = 0
}

class User(val login: String, var pass: String) extends BaseEntity {

  def this(id: Int, login: String, pass: String) = {
    this(login, pass)
    this.id = id
  }

}

class Quote(val picture: String, val date: Date, var marks: Set[Mark], val uploaderId: Int) extends BaseEntity {

  lazy val uploader: ManyToOne[User] = Model.userToQuotes.right(this)
  val rating = if (marks == null || marks.isEmpty) 0 else 1 //marks.map(_.points).sum / marks.size

//  var tags: Set[Tag]

  def this(id: Int, picture: String, date: Date, marks: Set[Mark], uploaderId: Int) {
    this(picture, date, marks, uploaderId)
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

  val userToQuotes = oneToManyRelation(users, quotes).via((u,q) => u.id === q.uploaderId)

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

    quotes.insert(new Quote(1, "pic", new Date(), Set(), 1))
  }

}