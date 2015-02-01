import server.Server
import store.{FileStore, Store, MemStore}

case class Config(port: Int, store: Store)

object sks extends App {

  val cfg = parseArgs()
  new Server(cfg.port, cfg.store)


  def parseArgs(): Config = {

    def get(flag: String): Option[String] = {
      val arg = s"-$flag="
      args.find(_.startsWith(arg)).map(_.replaceFirst(arg, ""))
    }

    val port = get("port").map(_.toInt).getOrElse(12121)

    val store = {
      val storeParts = get("store").getOrElse("mem").split(":")
      storeParts(0) match {
        case "mem" => new MemStore
        case "dir" => new FileStore(storeParts(1))
        case _ => sys.error("Invalid store param")
      }
    }

    Config(port, store)
  }

}
