package org.lizeo.backend.domains.stations.services

import com.sksamuel.elastic4s.ElasticDsl
import com.sksamuel.elastic4s.ElasticDsl._
import org.lizeo.backend.core.elasticsearch.repositories.EsRepository
import org.lizeo.backend.domains.stations.StationModel
import org.lizeo.backend.domains.stations.queries.UpdateStationLocationQuery

import scala.collection.JavaConverters.iterableAsScalaIterable

/**
  * Created by nico on 02/10/17.
  */
object StationServiceES extends StationService {
  val repository = new EsRepository[StationModel]("stations", "station")

  def getStationById(id: String): Option[StationModel] = repository.getById[StationModel](id)

  def getStations: Iterable[StationModel] = {
    repository.all[StationModel]()
  }

  def getStationsByDealer(dealerId: String): Iterable[StationModel] = {
    repository.all(ElasticDsl.termQuery("dealerId", dealerId))
  }

  def getStationsByCountry(countryCode: String): Iterable[StationModel] = {
    repository.all(ElasticDsl.termQuery("countryCode", countryCode))
  }

  def countStationsByDealers(): Map[String, Long] = {
    val aggField = "dealerId"
    val res = repository.run(ElasticDsl.search(repository.path).aggs(termsAggregation(aggField).field(aggField).size(1000)).size(0)).aggregations
    Map[String, Long](iterableAsScalaIterable(res.termsResult(aggField).getBuckets).toSeq.map( b => {
      b.getKeyAsString -> b.getDocCount
    }) : _*)
  }

  def createStation(station: StationModel): Unit = {
    repository.index(station)
  }

  def updateStationLocation(stationId: String, query: UpdateStationLocationQuery): Unit = {
    repository.updateById[StationModel](stationId, _.copy(
      latitude = query.latitude,
      longitude = query.longitude,
      countryCode = query.countryCode,
      address = query.address,
      postalCode = query.postalCode,
      city = query.city))
  }

  def updateStationDealer(stationId: String, dealerId: String): Unit = {
    repository.updateById[StationModel](stationId, _.copy(dealerId = dealerId))
  }

  def updateStationName(stationId: String, stationName: String): Unit = {
    repository.updateById[StationModel](stationId, _.copy(stationName = stationName))
  }

  def existsStationId(stationId: String): Boolean = getStationById(stationId).isDefined

  def resetDb(): Unit = {
    repository.deleteAll
    repository.run(
      createIndex(repository.indexName).mappings(
        mapping(repository.typeName).as(
          keywordField("id"),
          keywordField("dealerId"),
          keywordField("countryCode"),
          textField("stationName"),
          textField("address"),
          keywordField("postalCode"),
          textField("city"),
          doubleField("latitude"),
          doubleField("longitude")
        )
      )
    )
  }
}
