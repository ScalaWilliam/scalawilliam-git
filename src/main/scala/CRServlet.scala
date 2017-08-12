import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.options.MutableDataSet
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.treewalk.filter.PathFilter
import org.scalatra._

import scala.util.Try

/**
  * This is only ugly temporary code.
  */
class CRServlet(repository: Repository) extends ScalatraServlet {

  private def postRequestForm = {
    <form action="/post-request/" method="post">
      <input type="text" name="title" placeholder="Title of Change Request"/>
      <hr/>
      <textarea name="description" placeholder="Description"></textarea> <hr/>
      <button type="submit">Create Change Request</button>
    </form>
  }

  private def readme = {
    val options = new MutableDataSet()
    val parser = Parser.builder(options).build
    val renderer = HtmlRenderer.builder(options).build
    val lastCommitId = repository.resolve("HEAD")
    val revWalk = new RevWalk(repository)
    try {
      val commit = revWalk.parseCommit(lastCommitId)
      val treeWalk = new TreeWalk(repository)
      try {
        treeWalk.addTree(commit.getTree)
        treeWalk.setFilter(PathFilter.create("README.md"))
        require(treeWalk.next())
        val loader = repository.open(treeWalk.getObjectId(0))
        val document = parser.parse(new String(loader.getBytes, "UTF-8"))
        renderer.render(document)
      } finally treeWalk.close()
    } finally revWalk.close()
  }

  private def cloneInstruction = {
    <section>
      <h2>Clone main repository</h2>
      <p>Note: this URL will change.</p>
      <pre>
        <code>git clone https://git.digitalocean.scalawilliam.com/git/some-repo</code>
      </pre>
    </section>
  }

  private def listBranches = {
    import collection.JavaConverters._
    val g = new Git(repository)
    try {
      <ul>
        {g.branchList().call().asScala.map { b =>
        <li>
          {b.getName}{b.getLeaf.toString}
        </li>
      }}
      </ul>
    } finally g.close()
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

    val xml = <html>
      <head>
        <link rel="self" href={CRServlet.myUrl}/>
        <link rel="hub" href={CRServlet.switchboardUrl}/>
      </head>
      <body>
        <h1>Future of Git</h1>{cloneInstruction}{postRequestForm}<hr/>{listBranches}<hr/>{notesH.map { n =>
        <div>
          {n}
        </div>
      }}<section id="readme">
        <h2>README</h2>-readme-

      </section>
      </body>
    </html>

    contentType = "text/html"
    xml.toString.replaceAllLiterally("-readme-", Try(readme).toOption.getOrElse(""))
  }

}

object CRServlet {
  val myUrl = "https://git.digitalocean.scalawilliam.com/"
  val switchboardUrl = "https://switchboard.p3k.io/"
}
