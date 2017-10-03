package org.lizeo.backend.domains.stations.queries

/**
  * Created by nico on 02/10/17.
  */
case class CreateStationQuery(
  dealerId: Long,
  countryCode: String,
  stationName: String,
  latitude: Double,
  longitude: Double,
  address: String,
  postalCode: String,
  city: String
)
