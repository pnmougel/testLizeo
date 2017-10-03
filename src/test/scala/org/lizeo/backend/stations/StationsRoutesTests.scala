package org.lizeo.backend.stations

import java.io.File

import akka.http.scaladsl.model.{ContentTypes, Multipart, StatusCodes}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.lizeo.backend.core.json.JacksonSupport
import org.lizeo.backend.domains.stations.{StationModel, StationRoutes}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

/**
  * Created by nico on 03/10/17.
  */
class StationsRoutesTests extends WordSpec with Matchers with ScalatestRouteTest with JacksonSupport with BeforeAndAfterAll {

  "Endpoint DELETE /stations" should {
    "remove all stations" in {
      Delete("/stations") ~> StationRoutes.routes ~> check {
        status shouldEqual StatusCodes.OK
      }
    }
  }

  "Endpoint POST /stations" should {
    "create a new station" in {
      val request = StationModel(
        id = "0",
        dealerId = "dealerId",
        countryCode = "countryCode",
        stationName = "stationName",
        latitude = 0D,
        longitude = 0D,
        address = "address",
        postalCode = "postCode",
        city = "city")
      Post("/stations", request) ~> StationRoutes.routes ~> check {
        status shouldEqual StatusCodes.OK
      }
    }
  }

  "Endpoint POST /stations/csv" should {
    val file = new File(getClass.getClassLoader().getResource("stationsTest.csv").getFile)
    val formData = Multipart.FormData.fromPath("csv", ContentTypes.`text/csv(UTF-8)`, file.toPath)

    "Respond with status 200" in {
      Post("/stations/csv", formData) ~> StationRoutes.routes ~> check {
        status shouldEqual StatusCodes.OK
      }
    }
  }

  "Endpoint GET /stations" should {
    "return the list of stations" in {
      Get("/stations") ~> StationRoutes.routes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Seq[StationModel]].size shouldEqual(0)
      }
    }
  }

  "Endpoint GET /stations/id/{id}" should {
    "return a BadRequest error if the id is not found" in {
      Get("/stations/id/0") ~> StationRoutes.routes ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }
  }
}
