package com.qbrainx.service

import scala.util.matching.Regex


import java.io.{InputStream, PrintWriter}
import scala.util.{Failure, Success, Try}


trait Intiator {
  def isValidIp(line: String): Boolean = {
    val ipRegex: Regex = """.*?(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3}).*""".r
    ipRegex.pattern.matcher(line.split(",")(0)).matches()
  }

  def openInputStream:InputStream = getClass.getResourceAsStream("/log.csv")

  def writeToOutputFile(lines: Iterable[String]): Any = {

     val write: PrintWriter = new PrintWriter("output.txt")
     Try(write) match {
       case Success(value) => value.println(lines.foldRight("")((prev, current) => prev + "," + current))
                               write.close()
       case Failure(exception) => exception.printStackTrace()

     }
  }

}
