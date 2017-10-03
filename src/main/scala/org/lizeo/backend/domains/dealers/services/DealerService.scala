package org.lizeo.backend.domains.dealers.services

import org.lizeo.backend.domains.dealers.DealerModel

/**
  * Created by nico on 02/10/17.
  */
trait DealerService {
  def createDealer(dealer: DealerModel)

  def existsDealerId(dealerId: String): Boolean
}
