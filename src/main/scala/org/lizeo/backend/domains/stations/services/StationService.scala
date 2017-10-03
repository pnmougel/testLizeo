package org.lizeo.backend.domains.stations.services

import org.lizeo.backend.domains.stations.StationModel
import org.lizeo.backend.domains.stations.queries.{CreateStationQuery, UpdateStationLocationQuery}

/**
  * Created by nico on 02/10/17.
  */

trait StationService {
  def getStationById(id: String): Option[StationModel]

  def getStations: Iterable[StationModel]

  def getStationsByDealer(dealerId: String): Iterable[StationModel]

  def getStationsByCountry(countryCode: String): Iterable[StationModel]

  def countStationsByDealers(): Map[String, Long]

  def createStation(station: StationModel): Unit

  def updateStationLocation(stationId: String, query: UpdateStationLocationQuery): Unit

  def updateStationDealer(stationId: String, dealerId: String): Unit

  def updateStationName(stationId: String, stationName: String): Unit

  def existsStationId(stationId: String): Boolean

  def resetDb(): Unit
}
