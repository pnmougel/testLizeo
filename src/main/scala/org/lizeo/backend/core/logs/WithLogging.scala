package org.lizeo.backend.core.logs

import org.apache.logging.log4j.{LogManager, Logger}

/**
  * Created by nico on 02/10/17.
  */
trait WithLogging {
  val log: Logger = LogManager.getLogger(getClass)
}
