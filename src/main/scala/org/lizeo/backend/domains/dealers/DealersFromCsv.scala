package org.lizeo.backend.domains.dealers

import org.lizeo.backend.core.csv.LoadableFromCsv

import scala.util.Try

/**
  * Created by nico on 03/10/17.
  */
object DealersFromCsv extends LoadableFromCsv[DealerModel] {
  def csvRowToEntry(row: Seq[String]): Either[String, DealerModel] = {
    if(row.length != 3) {
      Left(s"Invalid number of columns, expected 3, found ${row.length}")
    } else {
      (for(dealerType <- Try(row(2).toInt).toOption) yield {
        Right(DealerModel(row.head, row(1), dealerType))
      }).getOrElse(Left("Unable to build instance an instance of DealerModel"))
    }
  }
}
