package org.lizeo.backend.domains.stations

import org.lizeo.backend.core.elasticsearch.EntityWithId

/**
  * Created by nico on 30/09/17.
  */
case class StationModel (
  id: String,
  dealerId: String,
  countryCode: String,
  stationName: String,
  latitude: Double,
  longitude: Double,
  address: String,
  postalCode: String,
  city: String) extends EntityWithId
