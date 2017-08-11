import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.scalatra._

class CRServlet(repository: Repository) extends ScalatraServlet {
  get("/") {
    val notes = new Git(repository)
      .notesList()
      .setNotesRef("refs/notes/change-requests")
      .call()
    import collection.JavaConverters._

    notes.asScala
      .map { n =>
        val s = new String(repository.open(n.getData).getBytes, "UTF-8")
        s"${n} -> $s"
      }
      .mkString("<br>")
  }
}
