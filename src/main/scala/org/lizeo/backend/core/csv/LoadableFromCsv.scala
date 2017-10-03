package org.lizeo.backend.core.csv

import java.io.File

import com.github.tototoshi.csv.CSVReader

/**
  * Created by nico on 02/10/17.
  */
trait LoadableFromCsv[T] {
  def loadFromCsvFile(file: File): Seq[Either[CsvParsingError, T]] = {
    val reader = CSVReader.open(file)
    val lines = reader.all()
    reader.close()
    for ((row, idx) <- lines.zipWithIndex; if idx != 0) yield {
      csvRowToEntry(row) match {
        case Left(error) => Left(CsvParsingError(idx, error))
        case Right(v) => Right(v)
      }
    }
  }

  def csvRowToEntry(row: Seq[String]): Either[String, T]
}

