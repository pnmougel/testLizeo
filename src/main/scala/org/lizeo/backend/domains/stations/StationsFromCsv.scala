package org.lizeo.backend.domains.stations

import org.lizeo.backend.core.csv.LoadableFromCsv

import scala.util.Try

/**
  * Created by nico on 03/10/17.
  */
object StationsFromCsv extends LoadableFromCsv[StationModel] {
  def csvRowToEntry(csvRow: Seq[String]): Either[String, StationModel] = {
    if(csvRow.length != 9) {
      Left(s"Invalid number of columns, expected 9, found ${csvRow.length}")
    } else {
      (for(latitude <- Try(csvRow(4).toDouble).toOption;
           longitude <- Try(csvRow(5).toDouble).toOption) yield {
        Right(StationModel(
          id = csvRow.head,
          dealerId = csvRow(1),
          countryCode = csvRow(2),
          stationName = csvRow(3),
          latitude = latitude,
          longitude = longitude,
          address = csvRow(6),
          postalCode = csvRow(7),
          city = csvRow(8)
        ))
      }).getOrElse(Left("Unable to build instance an instance of Station"))
    }
  }
}
