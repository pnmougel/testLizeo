package org.lizeo.backend.core.routes

import akka.http.scaladsl.server.{Directives, Route}
import org.lizeo.backend.core.json.JacksonSupport
import org.lizeo.backend.core.logs.WithLogging

/**
  * Created by nico on 02/10/17.
  */
trait DefaultAppRoute extends Directives with JacksonSupport with WithLogging {
  val routes: Route
}
