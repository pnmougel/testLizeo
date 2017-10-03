package org.lizeo.backend.core.elasticsearch.repositories

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.IndexAndType
import org.lizeo.backend.core.elasticsearch._

/**
  * Created by nico on 02/10/17.
  */
class EsRepository[T <: EntityWithId](val indexName: String, val typeName: String)
  extends IndexableRepository[T]
    with IterableRepository[T]
    with UpdatableRepository[T]
    with GetableRepository[T]
    with DeletableRepository {

  val path: IndexAndType = indexName / typeName
}
