import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ DefaultServlet, ServletContextHandler }
import org.eclipse.jetty.webapp.WebAppContext
import org.scalatra.servlet.ScalatraListener


/**Class with a main method to launch the app when deployed to Heroku.
 * 
 */
object JettyLauncher {
  def main(args: Array[String]) {
    val port = if(System.getenv("PORT") != null) System.getenv("PORT").toInt else 8080

    val server = new Server(port)
    val context = new WebAppContext()
    context.setContextPath("/")
    context.setResourceBase("src/main/webapp")

    context.setEventListeners(Array(new ScalatraListener))
    context.addServlet(classOf[com.darylweir.depservice.CoreServlet],"/*")

    server.setHandler(context)

    server.start
    server.join
  }
}