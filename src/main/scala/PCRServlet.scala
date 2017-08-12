import java.time.Instant
import java.time.format.DateTimeFormatter

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevWalk
import org.scalatra._
import play.api.libs.json.{JsString, Json}
import scala.collection.JavaConverters._

class PCRServlet(repository: Repository) extends ScalatraServlet {

  post("/") {
    val title = params.getAs[String]("title")
    val description = params.getAs[String]("description")

    val jsonObj = Json.obj(
      "title" -> JsString(title.get),
      "description" -> JsString(description.get),
      "by" -> JsString("William"),
      "created_time" -> JsString(
        DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
    )

    val refId = {
      val head = repository.exactRef("refs/heads/master")
      val walk = new RevWalk(repository)
      try walk.parseCommit(head.getObjectId)
      finally walk.close()
    }

    val newNote = new Git(repository)
      .notesAdd()
      .setObjectId(refId)
      .setNotesRef("refs/notes/change-requests")
      .setMessage(jsonObj.toString)
      .call()

    newNote.toString
  }
}
