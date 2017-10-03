package org.lizeo.backend.core.rejections

/**
  * Created by nico on 10/12/16.
  */
case class ErrorMessage(cause: String, details: Map[String, Any] = Map())

