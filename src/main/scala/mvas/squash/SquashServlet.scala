package mvas.squash

import java.io.FileOutputStream
import java.util.Date

import mvas.squash.db.Hasher
import org.scalatra.Ok
import org.squeryl.PrimitiveTypeMode._

import scala.util.Random

class SquashServlet extends SquashStack {

  val ips = List("127.0.0.1", "0:0:0:0:0:0:0:1")

  before() {
    contentType = "text/html"
    if (!ips.contains(request.getRemoteHost)) {
      halt(403, "Go away! Your address is " + request.getRemoteHost + " and not accepted")
    }
    if (request.getSession.get("user").isEmpty && requestPath != "/login") {
      redirect("/login")
    }
  }

  get("/?") {
    redirect("quotes")
  }

  get("/logout") {
    request.getSession.invalidate()
    redirect("/login")
  }

  get("/login") {
    <form action="/login" method="POST">
      <input type="text" name="username"/>
      <input type="password" name="password"/>
      <input type="submit" value="Go"/>
    </form>
  }

  post("/login") {
    val username = params("username")
    val password = params("password")
    val found = from(Model.users)(u => where(u.login === username) select u)

    if (found.nonEmpty) {
      val usr = found.single
      if (Hasher.hash(password).equalsIgnoreCase(usr.pass)) {
        request.getSession.setAttribute("user", username)
        redirect("/")
      } else {
        redirect("/login")
      }
    } else {
      redirect("/login")
    }
  }

  // -------------- files --------------
  get("/image/:name") {
    response.setHeader("Content-Type", "image/png")
    Ok(new java.io.File("c:\\testdir\\" + params("name")))
  }

  // -------------- tags --------------

  get("/tags/add/?") {
    ssp("addTag", "title" -> "Add tag")
  }

  post("/tags/add/?") {
    val tag = request.parameters.get("name").get
    if (from(Model.tags)(t => where(t.name === tag) select t).isEmpty) {
      Model.tags.insert(new Tag(tag))
      "ok"
    } else {
      "duplicate"
    }

  }

  get("/tags/remove/:name/?") {
    if (from(Model.tags)(t => where(t.name === params("name")) select t).single.quotes.isEmpty) {
      Model.tags.deleteWhere(tag => tag.name === params("name"))
    }
    redirect("tags")
  }

  get("/tags/?") {
    val tags = from(Model.tags)(t => select(t)).toList
    ssp("tags", "tags" -> tags)
  }

  get("/tags/all") {
    from(Model.tags)(t => select(t)).toList.map(_.name).mkString(",,")
  }

  // -------------- quotes --------------

  get("/quotes/?") {
    val quotes = from(Model.quotes)(q => select(q)).toList
    ssp("quotes", "quotes" -> quotes, "category" -> "")
  }

  get("/quotes/category/:cat") {
    val quotes = from(Model.tags)(t => where(t.name === params("cat")) select t).single.quotes.toList
    ssp("quotes", "quotes" -> quotes, "category" -> params("cat"))
  }

  get("/quotes/category/:cat/rand") {
    val quotes = from(Model.tags)(t => where(t.name === params("cat")) select t).single.quotes.toList
    val rand = quotes(Random.nextInt(quotes.size))
    ssp("quote", "quote" -> rand, "cat" -> true)
  }

  get("/quotes/add/?") {
    ssp("addQuote")
  }

  post("/quotes/add/?") {
    for (loaded <- fileMultiParams("image")) {
      val file = new java.io.File("c:\\testdir\\" + loaded.getName)
      val fos = new FileOutputStream(file)
      fos.write(loaded.get())
      fos.close()

      Model.quotes.insert(new Quote(loaded.getName, new Date(), Some(1)))
    }
    redirect("/quotes")
  }

  post("/quotes/addTag") {
    val quoteId = params("quote").toInt
    val tagName = params("tag")
    val quote = from(Model.quotes)(m => where(m.id === quoteId) select m).single
    if (quote.tags.toList.filter(_.name == tagName).isEmpty) {
      val tag = from(Model.tags)(t => where(t.name === tagName) select t).single
      quote.tags.associate(tag)
      "ok"
    } else {
      "duplicate"
    }
  }

  get("/quote/:id") {
    val quoteQ = from(Model.quotes)(q => where(q.id === params("id").toInt) select q)
    val quote = if (quoteQ.nonEmpty) {
      quoteQ.single
    } else {
      from(Model.quotes)(q => where(q.id === 1) select q).single
    }
    ssp("quote", "quote" -> quote, "cat" -> false)
  }
}
