package org.lizeo.backend.stations

import java.io.File

import org.lizeo.backend.domains.stations.StationsFromCsv
import org.lizeo.backend.domains.stations.queries.UpdateStationLocationQuery
import org.lizeo.backend.domains.stations.services.StationServiceInMemory
import org.scalatest.{Matchers, WordSpec}

/**
  * Created by nico on 03/10/17.
  */
class StationServiceInMemoryTest extends WordSpec with Matchers {
  val testDataFile = new File("data/stationsTest.csv")
  val service = StationServiceInMemory

  StationsFromCsv.loadFromCsvFile(testDataFile).foreach( e => {
    service.createStation(e.right.get)
  })

  "Searching all stations" should {
    "return all stations" in {
      val stations = service.getStations
      stations.size should be(3)
    }
  }

  "Getting a station by id" should {
    "return the station with the corresponding id" in {
      val station = service.getStationById("47529")
      station should not be empty
    }

    "return None if the id does not exists" in {
      val station = service.getStationById("")
      station shouldBe empty
    }
  }

  "Updating a station location" should {
    "allow to retrieve the station with the modified values" in {
      val query = UpdateStationLocationQuery(
        latitude = 0D,
        longitude = 0D,
        address = "address",
        postalCode = "postalCode",
        city = "city",
        countryCode = "countryCode"
      )
      service.updateStationLocation("47529", query)
      service.getStationById("47529").map { station =>
        station.latitude should be(query.latitude)
        station.longitude should be(query.longitude)
        station.address should be(query.address)
        station.postalCode should be(query.postalCode)
        station.city should be(query.city)
        station.countryCode should be(query.countryCode)
      }
    }
  }
}
