package org.lizeo.backend.domains.dealers

import org.lizeo.backend.core.elasticsearch.EntityWithId

/**
  * Created by nico on 30/09/17.
  */
case class DealerModel(
  id: String,
  name: String,
  dealerType: Int) extends EntityWithId
