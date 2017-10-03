package org.lizeo.backend.domains.dealers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import org.lizeo.backend.core.rejections.ApiRejection
import org.lizeo.backend.core.routes.DefaultAppRoute
import org.lizeo.backend.domains.dealers.services.{DealerService, DealerServiceInMemory}
import org.lizeo.backend.domains.stations.StationRoutes.{complete, path, post, stationService, uploadedFile}
import org.lizeo.backend.domains.stations.StationsFromCsv
import org.lizeo.backend.domains.stations.services.{StationService, StationServiceInMemory}

/**
  * Created by nico on 02/10/17.
  */
object DealerRoutes extends DefaultAppRoute {
  private val stationService: StationService = StationServiceInMemory
  private val dealerService: DealerService = DealerServiceInMemory

  private val getStationsByDealer: Route = (get & path(Segment / "stations")) { dealerId =>
    if(dealerService.existsDealerId(dealerId)) {
      complete(stationService.getStationsByDealer(dealerId))
    } else {
      reject(ApiRejection(s"Missing dealerId $dealerId", StatusCodes.NotFound))
    }
  }


  private val initFromCsv: Route = (post & path("csv") & uploadedFile("csv")) { case (_, file) =>
    complete {
      val res = DealersFromCsv.loadFromCsvFile(file)
      if (res.exists(_.isLeft)) {
        val errors = res.filter(_.isLeft).map(_.left.get)
        (StatusCodes.BadRequest, errors)
      } else {
        for (entry <- res; dealer <- entry) {
          dealerService.createDealer(dealer)
        }
      }
    }
  }

  private val createDealerRoute: Route = (post & entity(as[DealerModel])) { dealer =>
    pathEndOrSingleSlash & complete {
      dealerService.createDealer(dealer)
    }
  }

  val routes: Route = pathPrefix("dealers") {
    getStationsByDealer ~ createDealerRoute ~ initFromCsv
  }
}
