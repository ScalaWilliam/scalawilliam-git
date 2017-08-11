import java.io.File

import com.typesafe.config.ConfigFactory
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletHandler, ServletHolder}
import org.eclipse.jgit.http.server.GitServlet
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

/**
  * > git clone http://localhost:8080/any-repo-name
  */
object MainServer extends App {

  val configuration = ConfigFactory.load()
  val gitDir = new File(configuration.getString("gitdir"))
  val httpPort = configuration.getInt("httpPort")

  val repository = {
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
  val servletHolder = new ServletHolder(gitServlet)
  servletHandler.addServletWithMapping(servletHolder, "/*")
  server.start()
  server.join()

}
