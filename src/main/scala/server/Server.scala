package server

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.server.{Request, Server => Jetty}
import store.Store

case class Server(port: Int, store: Store) {

  private val server = {
    val server = new Jetty(port)
    server.setHandler(new Handler(this))
    server.start()
    server
  }

  def join() = server.join()

}
object Server {

  def startAndListen(port: Int, store: Store): Unit = {
    val s = Server(port, store)
    s.join()
  }

}

class Handler(server: Server) extends AbstractHandler {

  val Key = "/(.*)".r

  override def handle(target: String, baseReq: Request, req: HttpServletRequest, res: HttpServletResponse): Unit = {
    val Key(key) = target
    if (key.isEmpty) {
      res.sendError(400)
    } else {
      baseReq.getMethod match {
        case "GET" => get(key, req, res)
        case "POST" => post(key, req, res)
        case _ => res.sendError(400)
      }
    }
    baseReq.setHandled(true)
  }

  def post(key: String, req: HttpServletRequest, res: HttpServletResponse) = {
    val reader = req.getReader
    val body = Stream.continually(reader.readLine()).takeWhile(_ != null).mkString("\n")
    server.store.add(key, body)
  }

  def get(key: String, req: HttpServletRequest, res: HttpServletResponse) = {
    server.store.get(key) match {
      case Some(value) => res.getWriter.print(value)
      case None => res.sendError(404)
    }
  }

}