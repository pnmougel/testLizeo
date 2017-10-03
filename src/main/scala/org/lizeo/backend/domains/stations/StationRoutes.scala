package org.lizeo.backend.domains.stations

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import org.lizeo.backend.core.json.JacksonSupport
import org.lizeo.backend.core.rejections.ApiRejection
import org.lizeo.backend.domains.dealers.services.{DealerService, DealerServiceInMemory}
import org.lizeo.backend.domains.stations.queries.UpdateStationLocationQuery
import org.lizeo.backend.domains.stations.services.{StationService, StationServiceInMemory}

/**
  * Created by nico on 02/10/17.
  */
object StationRoutes extends Directives with JacksonSupport {
   val stationService: StationService = StationServiceInMemory
   val dealerService: DealerService = DealerServiceInMemory

  private val stationRoutes = pathPrefix("stations")

  private val getStations: Route = {
    pathEndOrSingleSlash & complete(stationService.getStations)
  }


  private val getStationById: Route = path("id" / Segment) { stationId =>
    pathEndOrSingleSlash {
      stationService.getStationById(stationId).map { station =>
        complete(station)
      }.getOrElse(reject(ApiRejection(s"Missing stationId $stationId", StatusCodes.NotFound)))
    }
  }

  private val getStationsByCountryCode: Route = (path("country") & parameters('country.as[String].?("FR"))) { countryCode =>
    pathEndOrSingleSlash & complete(stationService.getStationsByCountry(countryCode))
  }

  private val countStationsByDealer: Route = path("countBy" / "dealer") {
    pathEndOrSingleSlash & complete(stationService.countStationsByDealers())
  }

  private val getRoutes: Route = get {
    getStations ~ getStationById ~ getStationsByCountryCode ~ countStationsByDealer
  }

  private val createRoute: Route = (post & entity(as[StationModel])) { station =>
    pathEndOrSingleSlash & complete {
      stationService.createStation(station)
    }
  }

  private val initFromCsv: Route = (post & path("csv") & uploadedFile("csv")) { case (_, file) =>
    complete {
      val res = StationsFromCsv.loadFromCsvFile(file)
      if (res.exists(_.isLeft)) {
        val errors = res.filter(_.isLeft).map(_.left.get)
        (StatusCodes.BadRequest, errors)
      } else {
        for (entry <- res; station <- entry) {
          stationService.createStation(station)
        }
      }
    }
  }

  private val updateLocationRoute: Route = (put & path(Segment / "location") & entity(as[UpdateStationLocationQuery])) { (stationId, query) =>
    complete(stationService.updateStationLocation(stationId, query))
  }

  private val updateDealerIdRoute: Route = (put & path(Segment / "dealerId" / Segment)) { (stationId, dealerId) =>
    if(dealerService.existsDealerId(dealerId)) {
      complete(stationService.updateStationDealer(stationId, dealerId))
    } else {
      reject(ApiRejection(s"dealerId parameter ${dealerId} does not exists"))
    }
  }


  private val updateStationNameRoute: Route = (put & path(Segment / "name" / Segment)) { (stationId, stationName) =>
    if(stationName.nonEmpty) {
      complete(stationService.updateStationName(stationId, stationName))
    } else {
      reject(ApiRejection(s"stationName parameter should not be empty"))
    }
  }

  private val updateRoutes: Route = updateLocationRoute ~ updateDealerIdRoute

  private val resetRoute: Route = delete & complete(stationService.resetDb())

  val routes: Route = stationRoutes {
    getRoutes ~ createRoute ~ initFromCsv ~ updateRoutes ~ resetRoute
  }
}
