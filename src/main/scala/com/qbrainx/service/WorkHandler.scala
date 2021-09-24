package com.qbrainx.service

import com.qbrainx.model.Worker.{Date, Log}

trait WorkHandler {
  def convertToLog(line: String): Log = line.split(",").toList match {
    case ip :: time :: url :: status :: _ =>
      Log(ip, convertToDate(time), url, status)
  }

  def convertToDate(time: String): Date = time.substring(1).split("/").toList match {
    case date :: month :: yearAndTime :: _ =>
      yearAndTime.split(":").toList match {
        case year :: rest => Date(year.toInt, month, date.toInt, rest.mkString(":"))
      }
  }

}
