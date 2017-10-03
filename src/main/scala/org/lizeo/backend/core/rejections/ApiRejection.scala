package org.lizeo.backend.core.rejections

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Rejection

/**
  * Created by nico on 20/12/16.
  */
case class ApiRejection(message: String, status: StatusCode = StatusCodes.BadRequest) extends Rejection
