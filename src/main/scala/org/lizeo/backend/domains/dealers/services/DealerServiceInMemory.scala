package org.lizeo.backend.domains.dealers.services

import org.lizeo.backend.domains.dealers.DealerModel
import org.lizeo.backend.domains.stations.StationModel

import scala.collection.mutable

/**
  * Created by nico on 02/10/17.
  */
object DealerServiceInMemory extends DealerService {
  val dealers = mutable.HashMap[String, DealerModel]()

  def createDealer(dealer: DealerModel): Unit = {
    dealers(dealer.id) = dealer
  }

  def getDealerById(id: String): Option[DealerModel] = dealers.get(id)

  def existsDealerId(dealerId: String): Boolean = getDealerById(dealerId).isDefined

  def getDealers() = dealers.values
}
