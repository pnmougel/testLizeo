package org.lizeo.backend.domains.stations.services

import org.lizeo.backend.domains.stations.StationModel
import org.lizeo.backend.domains.stations.queries.UpdateStationLocationQuery

import scala.collection.mutable

/**
  * Created by nico on 02/10/17.
  */
object StationServiceInMemory extends StationService {
  private val stations = mutable.HashMap[String, StationModel]()

  def getStationById(id: String): Option[StationModel] = stations.get(id)

  def getStations: Iterable[StationModel] = stations.values

  def getStationsByDealer(dealerId: String): Iterable[StationModel] = {
    stations.values.filter(_.dealerId == dealerId)
  }

  def getStationsByCountry(countryCode: String): Iterable[StationModel] = {
    stations.values.filter(_.countryCode == countryCode)
  }

  def countStationsByDealers(): Map[String, Long] = {
    stations.values.groupBy(_.dealerId).mapValues(_.size)
  }

  def createStation(station: StationModel): Unit = {
    stations(station.id) = station
  }

  def existsStationId(stationId: String): Boolean = stations.get(stationId).isDefined

  def updateStationLocation(stationId: String, query: UpdateStationLocationQuery): Unit = {
    stations.get(stationId).foreach { station =>
      stations(stationId) = station.copy(
        latitude = query.latitude,
        longitude = query.longitude,
        countryCode = query.countryCode,
        address = query.address,
        postalCode = query.postalCode,
        city = query.city)
    }
  }


  def updateStationDealer(stationId: String, dealerId: String): Unit = {
    stations.get(stationId).foreach { station =>
      stations(stationId) = station.copy(dealerId = dealerId)
    }
  }

  def updateStationName(stationId: String, stationName: String): Unit = {
    stations.get(stationId).foreach { station =>
      stations(stationId) = station.copy(stationName = stationName)
    }
  }

  def resetDb(): Unit = stations.clear()
}
