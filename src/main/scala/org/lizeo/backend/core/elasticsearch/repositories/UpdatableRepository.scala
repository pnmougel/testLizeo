package org.lizeo.backend.core.elasticsearch.repositories

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{ElasticDsl, IndexAndType}
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy
import org.lizeo.backend.core.elasticsearch.{ElasticExecutable, EntityWithId}
import org.lizeo.backend.core.json.ElasticJackson
import org.lizeo.backend.core.json.ElasticJackson.Implicits._

/**
  * Created by nico on 10/12/16.
  */
trait UpdatableRepository[T <: EntityWithId] extends ElasticExecutable {
  val path: IndexAndType

  def updateById[T <: EntityWithId](id: String, f: T => T)(implicit mf: scala.reflect.Manifest[T]): Option[T] = {
    val item = run(ElasticDsl.get(id).from(path)).safeToOpt[T]
    item match {
      case Some(Right(prevItem)) => {
        val newItem: T = f(prevItem)
        run(ElasticDsl.update(id).in(path).doc[T](newItem)(ElasticJackson.Implicits.JacksonJsonUpdateIndexable).refresh(RefreshPolicy.IMMEDIATE))
        Some(newItem)
      }
      case Some(Left(exception)) => None
      case _ => None
    }
  }
}