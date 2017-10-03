package org.lizeo.backend.core.elasticsearch.repositories

import com.sksamuel.elastic4s.ElasticDsl.{matchAllQuery, searchScroll, _}
import com.sksamuel.elastic4s.searches.{QueryDefinition, RichSearchResponse}
import com.sksamuel.elastic4s.{ElasticDsl, IndexAndType}
import org.lizeo.backend.core.elasticsearch.{ElasticExecutable, EntityWithId}
import org.lizeo.backend.core.json.ElasticJackson.Implicits._

import scala.concurrent.duration.{FiniteDuration, _}

/**
  * Created by nico on 02/10/17.
  */
trait IterableRepository[T <: EntityWithId] extends ElasticExecutable {
  val path: IndexAndType

  def all[T](query: QueryDefinition = matchAllQuery(), duration: FiniteDuration = 60 seconds)(implicit mf: scala.reflect.Manifest[T]): Vector[T] = {

    var results = Vector[T]()
    iterateSearchResponses(query, duration) { response =>
      response.hits.foreach(hit => results +:= hit.to[T] )
    }
    results
  }

  protected def iterateSearchResponses(query: QueryDefinition, duration: FiniteDuration)
    (f: RichSearchResponse => Unit): Any = {
    var res = run(ElasticDsl.search(path).query(query).scroll(duration.toSeconds + "s"))
    while(res.hits.nonEmpty) {
      f(res)
      res = run(searchScroll(res.scrollId).keepAlive(duration))
    }
  }
}
