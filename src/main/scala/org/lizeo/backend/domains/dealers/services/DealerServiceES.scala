package org.lizeo.backend.domains.dealers.services

import org.lizeo.backend.core.elasticsearch.repositories.EsRepository
import org.lizeo.backend.domains.dealers.DealerModel
import org.lizeo.backend.domains.stations.StationModel

/**
  * Created by nico on 02/10/17.
  */
object DealerServiceES extends DealerService {
  val stationRepository = new EsRepository[StationModel]("stations", "station")
  val dealersRepository = new EsRepository[DealerModel]("dealers", "dealer")

  def createDealer(dealer: DealerModel) = dealersRepository.index(dealer)

  def getDealerById(id: String): Option[DealerModel] = dealersRepository.getById[DealerModel](id)

  def existsDealerId(dealerId: String): Boolean = getDealerById(dealerId).isDefined
}
