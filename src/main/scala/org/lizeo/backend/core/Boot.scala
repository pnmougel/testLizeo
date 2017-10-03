package org.lizeo.backend.core

import java.net.BindException

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import org.lizeo.backend.core.configuration.Config
import org.lizeo.backend.core.logs.WithLogging
import org.lizeo.backend.domains.dealers.DealerRoutes
import org.lizeo.backend.domains.stations.StationRoutes


/**
  * Created by nico on 30/09/17.
  */
object Boot extends Directives with WithLogging {
  def main(args: Array[String]): Unit = {
    import org.lizeo.backend.core.rejections.JsonRejectionHandlers.jsonRejectionHandler
    implicit val system = ActorSystem("system")
    implicit val materializer = ActorMaterializer()

    log.info(s"Starting app on port ${Config.serverPort}")
    try {
      Http().bindAndHandle(StationRoutes.routes ~ DealerRoutes.routes, "localhost", Config.serverPort)
    } catch {
      case e: BindException => {
        log.error(s"Unable to start server on port ${Config.serverPort}",  e)
      }
    }
  }
}
