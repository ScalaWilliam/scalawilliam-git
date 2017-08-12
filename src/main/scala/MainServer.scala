import java.io.File

import com.typesafe.config.ConfigFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletHandler, ServletHolder}
import org.eclipse.jgit.http.server.GitServlet
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

object MainServer extends App {

  val configuration = ConfigFactory.load()
  val gitDir = new File(configuration.getString("gitdir"))

  val httpPort = configuration.getInt("httpPort")

  val repository =
    if (!gitDir.exists()) {
      val r = FileRepositoryBuilder.create(gitDir)
      r.create(false)
      r
    } else {
      val repositoryBuilder = new FileRepositoryBuilder()
      repositoryBuilder.setGitDir(gitDir)
      repositoryBuilder.build()
    }

  val gitServlet = new GitServlet
  gitServlet.setRepositoryResolver { (_, _) =>
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
