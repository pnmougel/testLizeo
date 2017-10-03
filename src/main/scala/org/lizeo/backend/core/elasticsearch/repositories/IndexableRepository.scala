package org.lizeo.backend.core.elasticsearch.repositories

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.IndexAndType
import com.sksamuel.elastic4s.indexes.RichIndexResponse
import org.lizeo.backend.core.elasticsearch.{ElasticExecutable, EntityWithId}
import org.lizeo.backend.core.json.ElasticJackson.Implicits._

/**
  * Created by nico on 10/12/16.
  */
trait IndexableRepository[T <: EntityWithId] extends ElasticExecutable {
  val path: IndexAndType

  def index(item: T): RichIndexResponse = {
    run(indexInto(path).source[T](item).id(item.id))
  }

  def indexAndGenerateId(item: T): RichIndexResponse = {
    run(indexInto(path).source[T](item))
  }
}