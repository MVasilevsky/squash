package mvas.squash

import java.io.FileOutputStream
import java.util.Date

import org.scalatra.Ok
import org.squeryl.PrimitiveTypeMode._

class SquashServlet extends SquashStack {

  val ips = List("127.0.0.1", "0:0:0:0:0:0:0:1")

  before() {
    contentType = "text/html"
    if (!ips.contains(request.getRemoteHost)) {
      halt(403, "Go away! Your address is " + request.getRemoteHost + " and not accepted")
    }
    basicAuth()
  }

  get("/?") {
    redirect("quotes")
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
    Model.tags.insert(new Tag(tag))
    tag
  }

  get("/tags/remove/:id/?") {
    if (params("id") forall Character.isDigit) Model.tags.deleteWhere(tag => tag.id === params("id").toInt)
    redirect("tags")
  }

  get("/tags/?") {
    val tags = from(Model.tags)(t => select(t)).toList
    ssp("tags", "tags" -> tags)
  }

  // -------------- quotes --------------

  get("/quotes/?") {
    val quotes = from(Model.quotes)(q => select(q)).toList
    ssp("quotes", "quotes" -> quotes)
  }

  get("/quotes/add/?") {
    ssp("addQuote")
  }

  post("/quotes/add/?") {
    val loaded = fileParams("image")
    val file = new java.io.File("c:\\testdir\\" + loaded.getName)
    val fos = new FileOutputStream(file)
    fos.write(loaded.get())
    fos.close()

    Model.quotes.insert(new Quote(loaded.getName, new Date(), Some(1)))

    redirect("/quotes")
  }

  get("/quote/:id") {
    val quote = from(Model.quotes)(q => where(q.id === params("id").toInt) select(q)).single
    ssp("quote", "quote" -> quote)
  }
}
