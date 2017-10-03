package org.lizeo.backend.core.elasticsearch.repositories

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{ElasticDsl, IndexAndType}
import org.lizeo.backend.core.elasticsearch.{ElasticExecutable, EntityWithId}
import org.lizeo.backend.core.json.ElasticJackson.Implicits._

/**
  * Created by nico on 10/12/16.
  */
trait GetableRepository[T <: EntityWithId] extends ElasticExecutable {
  val path: IndexAndType

  def getById[T <: EntityWithId](id: String)(implicit mf: scala.reflect.Manifest[T]): Option[T] = {
    val item = run(ElasticDsl.get(id).from(path)).safeToOpt[T]
    println(item)
    item match {
      case Some(Right(prevItem)) => Some(prevItem)
      case Some(Left(exception)) => None
      case _ => None
    }
  }
}