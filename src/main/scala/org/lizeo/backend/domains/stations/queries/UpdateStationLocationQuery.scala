package org.lizeo.backend.domains.stations.queries

/**
  * Created by nico on 03/10/17.
  */
case class UpdateStationLocationQuery(
  latitude: Double,
  longitude: Double,
  address: String,
  postalCode: String,
  city: String,
  countryCode: String
) {
  require(-90 <= latitude && latitude <= 90, "latitude field must be between -90 and 90")
  require(-180 <= longitude && latitude <= 1800, "latitude field must be between -180 and 180")
  require(!address.isEmpty, "address field should not be empty")
  require(!postalCode.isEmpty, "postalCode field should not be empty")
  require(!city.isEmpty, "city field should not be empty")
  require(!countryCode.isEmpty, "countryCode field should not be empty")
}
