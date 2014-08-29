package mvas.squash

import java.io.FileOutputStream
import java.util.Date

import org.scalatra.{BadRequest, Ok}
import org.squeryl.PrimitiveTypeMode._

import scala.reflect.io.File

class SquashServlet extends SquashStack {

  val ips = List("127.0.0.1", "0:0:0:0:0:0:0:1")

  before() {
    contentType = "text/html"
    if (!ips.contains(request.getRemoteHost)) {
      halt(403, "Go away! Your address is " + request.getRemoteHost + " and not accepted")
    }
  }

  get("/?") {
    redirect("quote")
  }
  // -------------- files --------------
  get("/image/:name") {
    response.setHeader("Content-Type", "image/png")
    Ok(new java.io.File("c:\\testdir\\" + params("name")))
  }

  get("/simage/:name") {
    response.setHeader("Content-Type", "image/png")
    Ok(new java.io.File("c:\\testdir\\" + params("name")))
  }

  // -------------- tags --------------

  get("/tag/add/?") {
    ssp("addTag", "title" -> "Add tag")
  }

  post("/tag/add/?") {
    Model.tags.insert(new Tag(request.parameters.get("name").get))
    redirect("tag")
  }

  get("/tag/remove/:id/?") {
    if (params("id") forall Character.isDigit) Model.tags.deleteWhere(tag => tag.id === params("id").toInt)
    redirect("tag")
  }

  get("/tag/?") {
    val tags = from(Model.tags)(t => select(t)).toList
    ssp("tags", "tags" -> tags)
  }

  // -------------- quotes --------------

  get("/quote/?") {
    val quotes = from(Model.quotes)(q => select(q)).toList
    ssp("quotes", "quotes" -> quotes)
  }

  get("/quote/add/?") {
    ssp("addQuote")
  }

  post("/quote/add/?") {
    val loaded = fileParams("image")
    val file = new java.io.File("c:\\testdir\\" + loaded.getName)
    val fos = new FileOutputStream(file)
    fos.write(loaded.get())
    fos.close()

    Model.quotes.insert(new Quote(loaded.getName, new Date(), Set.empty, 1))

    redirect("/quote")
  }
}
