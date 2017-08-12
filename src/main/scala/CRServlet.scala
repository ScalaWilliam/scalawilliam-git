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

  private def cloneInstruction = {
    <section>
      <h2>Clone main repository</h2>
      <p>Note: this URL will change.</p>
      <pre><code>git clone https://git.digitalocean.scalawilliam.com/git/some-repo</code></pre>
    </section>
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
      {cloneInstruction}
      {postRequestForm}
      <hr/>
      {notesH.map{ n => <div>{n}</div>}}
    </body>
  }
}
