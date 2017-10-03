package org.lizeo.backend.core.elasticsearch

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.Executable

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by nico on 29/12/16.
  */
trait ElasticExecutable {
  def run[T, R, Q](esQuery: T, timeout: Duration = 10.seconds)(implicit executable: Executable[T, R, Q]): Q = {
    EsClient.client.execute(esQuery).await(timeout)
  }

  def runAsync[T, R, Q](esQuery: T)(implicit executable: Executable[T, R, Q]): Future[Q] = {
    EsClient.client.execute(esQuery)
  }
}
