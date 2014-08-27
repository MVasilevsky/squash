package mvas.squash

import org.squeryl.PrimitiveTypeMode._

class SquashServlet extends SquashStack {

  val ips = List("127.0.0.1", "0:0:0:0:0:0:0:1")

  before() {
    contentType = "text/html"
    if (!ips.contains(request.getRemoteHost)) {
      halt(403, "Go away! Your address is " + request.getRemoteHost + " and not accepted")
    }
  }

  get("/?") {
    redirect("tags")
  }

  get("/tags/add/?") {
    ssp("addTag.ssp", "title" -> "Add tag")
  }

  post("/tags/add/?") {
    Model.tags.insert(new Tag(request.parameters.get("name").get))
    redirect("tags")
  }

  //  tags
  get("/tags/?") {
    val tags = from(Model.tags)(m => select(m)).toList
    ssp("tags", "title" -> "Tags", "tags" -> tags)
  }

}
