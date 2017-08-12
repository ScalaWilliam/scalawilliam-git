import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.scalatra._

class CRServlet(repository: Repository) extends ScalatraServlet {

  private def postRequestForm = {
    <form action="/post-request/" method="post">
      <input type="text" name="title" placeholder="Title of Change Request"/>
        <hr/>
        <textarea name="description" placeholder="Description"></textarea><hr/>
        <button type="submit">Create Change Request</button>
    </form>
  }

  get("/") {
    val notes = new Git(repository)
      .notesList()
      .setNotesRef("refs/notes/change-requests")
      .call()
    import collection.JavaConverters._

    val notesH = notes.asScala
      .map { n =>
        val s = new String(repository.open(n.getData).getBytes, "UTF-8")
        s"${n} -> $s"
      }

    <body>
      {postRequestForm}
      <hr/>
      {notesH.map{ n => <div>{n}</div>}}
    </body>
  }
}
