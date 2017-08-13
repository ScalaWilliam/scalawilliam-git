import java.io.File
import java.util
import javax.servlet.http.HttpServletRequest

import com.typesafe.config.ConfigFactory
import org.apache.http.client.fluent.Request
import org.apache.http.message.BasicNameValuePair
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletHandler, ServletHolder}
import org.eclipse.jgit.http.server.GitServlet
import org.eclipse.jgit.http.server.resolver.DefaultReceivePackFactory
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.{PostReceiveHook, ReceiveCommand, ReceivePack}

/**
  * TODO support multiple repos.
  */
object MainServer extends App {

  val configuration = ConfigFactory.load()
  val gitDir = new File(configuration.getString("gitdir"))

  val httpPort = configuration.getInt("httpPort")

  val repository =
    if (!gitDir.exists()) {
      val r = FileRepositoryBuilder.create(gitDir)
      r.create(true)
      val config = r.getConfig
      config.setBoolean("http", null, "receivepack", true)
      config.save()
      r
    } else {
      val repositoryBuilder = new FileRepositoryBuilder()
      repositoryBuilder.setGitDir(gitDir)
      repositoryBuilder.build()
    }

  val gitServlet = new GitServlet

  /**
    * Send notifications to switchboard.
    */
  val postReceiveHook: PostReceiveHook =
    (rp: ReceivePack, commands: util.Collection[ReceiveCommand]) => {
      val response = Request
        .Post(CRServlet.switchboardUrl)
        .bodyForm(
          new BasicNameValuePair("hub.mode", "publish"),
          new BasicNameValuePair("hub.url", CRServlet.myUrl)
        )
        .execute()
      println(response)
      commands.forEach(c => println(c))
    }

  val packFactory = new DefaultReceivePackFactory {
    override def create(req: HttpServletRequest, db: Repository): ReceivePack = {
      val rp = super.create(req, db)
      rp.setPostReceiveHook(postReceiveHook)
      rp
    }
  }

  gitServlet.setReceivePackFactory(packFactory)

  gitServlet.setRepositoryResolver { (_, name) =>
    println(s"Repository name found = ${name}")
    repository.incrementOpen()
    repository
  }

  val server = new Server(httpPort)
  val servletHandler = new ServletHandler
  server.setHandler(servletHandler)

  val indexServlet = new CRServlet(repository)

  servletHandler.addServletWithMapping(new ServletHolder(gitServlet), "/git/*")
  servletHandler.addServletWithMapping(new ServletHolder(indexServlet), "/")
  servletHandler.addServletWithMapping(
    new ServletHolder(new PCRServlet(repository)),
    "/post-request/")
  server.start()
  server.join()

}
