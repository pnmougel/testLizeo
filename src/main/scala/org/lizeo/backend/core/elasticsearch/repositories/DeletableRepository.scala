package org.lizeo.backend.core.elasticsearch.repositories

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{ElasticDsl, IndexAndType}
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.lizeo.backend.core.elasticsearch.{ElasticExecutable, EntityWithId}
import org.lizeo.backend.core.json.ElasticJackson
import org.lizeo.backend.core.json.ElasticJackson.Implicits._

/**
  * Created by nico on 03/10/17.
  */
trait DeletableRepository extends ElasticExecutable {
  val path: IndexAndType

  def deleteAll = {
    run(ElasticDsl.deleteIndex(path.index))
  }
}