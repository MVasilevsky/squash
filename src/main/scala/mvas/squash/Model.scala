package mvas.squash

import java.util.Date

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.dsl.{CompositeKey2, ManyToMany, OneToMany, ManyToOne}

import org.squeryl.{Schema, KeyedEntity}

/**
 * Created by Max on 24.08.2014.
 */
//abstract class Model

class BaseEntity extends KeyedEntity[Int]{
  var id: Int = 0
}

class User(val login: String, var pass: String) extends BaseEntity {
  lazy val marks = Model.userToMarks.left(this)
  lazy val quotes = Model.userToQuotes.left(this)
  lazy val comments = Model.userToComments.left(this)
}

class Quote(val picture: String, val date: Date, val uploaderId: Option[Int]=None) extends BaseEntity {
  lazy val uploader = Model.userToQuotes.right(this)
  lazy val marks = Model.quoteToMarks.left(this)
  lazy val tags = Model.quotesToTags.left(this)
  lazy val comments = Model.quoteToComments.left(this)
  lazy val rating = if (marks == null || marks.isEmpty) 0 else marks.map(_.points).sum / marks.size
}

class Comment(val quoteId: Option[Int]=None, val text: String, val date: Date, val authorId: Option[Int]=None) {
  lazy val quote = Model.quoteToComments.right(this)
  lazy val author = Model.userToComments.right(this)
}

class Tag(val name: String) extends BaseEntity {
  lazy val quotes = Model.quotesToTags.right(this)
}

class QuotesTags(val quoteId: Int, val tagId: Int) extends KeyedEntity[CompositeKey2[Int,Int]] {
  def id = compositeKey(quoteId, tagId)
}

class Mark(val quoteId: Option[Int]=None, val points: Int, val userId: Option[Int]=None, val date: Date) extends BaseEntity {
  lazy val user = Model.userToMarks.right(this)
  lazy val quote = Model.quoteToMarks.right(this)
}

object Model extends Schema {
  val users = table[User]
  val quotes = table[Quote]
  val tags = table[Tag]
  val marks = table[Mark]
  val comments = table[Comment]

  val userToQuotes = oneToManyRelation(users, quotes).via((u,q) => u.id === q.uploaderId)
  val quoteToMarks = oneToManyRelation(quotes, marks).via((q,m) => q.id === m.quoteId)
  val quotesToTags = manyToManyRelation(quotes, tags).via[QuotesTags]((q,t,qt) => (q.id === qt.quoteId, t.id === qt.tagId))
  val quoteToComments = oneToManyRelation(quotes, comments).via((q,c) => q.id === c.quoteId)
  val userToMarks = oneToManyRelation(users, marks).via((u,m) => u.id === m.userId)
  val userToComments = oneToManyRelation(users, comments).via((u,c) => u.id === c.authorId)


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
    tags.insert(new Tag("Groushee"))
    tags.insert(new Tag("Sleevy"))
    tags.insert(new Tag("Mini kolby"))

    users.insert(new User("mvas", "49FCD027468ACB7C26D56BF014469116D54CCD5D994DE843466800333B5E4758"))
    users.insert(new User("dab", "49FCD027468ACB7C26D56BF014469116D54CCD5D994DE843466800333B5E4758"))
    users.insert(new User("rsd", "49FCD027468ACB7C26D56BF014469116D54CCD5D994DE843466800333B5E4758"))

    quotes.insert(new Quote("1.jpeg", new Date(), Some(1)))
    quotes.insert(new Quote("2.png", new Date(), Some(1)))
    quotes.insert(new Quote("3.png", new Date(), Some(1)))
    quotes.insert(new Quote("4.png", new Date(), Some(1)))
    quotes.insert(new Quote("5.png", new Date(), Some(1)))
    quotes.insert(new Quote("6.png", new Date(), Some(1)))
  }

}