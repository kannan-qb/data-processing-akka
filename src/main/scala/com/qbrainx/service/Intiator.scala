package com.qbrainx.service

import scala.util.matching.Regex


import java.io.{InputStream, PrintWriter}
import scala.util.{Failure, Success, Try}


trait Intiator {
  def isValidIp(line: String): Boolean = {
    val ipRegex: Regex = """.*?(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3}).*""".r
    ipRegex.pattern.matcher(line.split(",")(0)).matches()
  }

  def openInputStream:InputStream = getClass.getResourceAsStream("/weblog.csv")

  def writeToOutputFile(lines: Iterable[String]): Any = {
    val path: String = getClass.getResource("/output.txt").getPath
     val write = new PrintWriter(path)
     Try(write) match {
       case Success(value) => val header = lines.foldRight("")((prev, current) => prev + "," + current)
                               value.println(header)
       case Failure(exception) => exception.printStackTrace()

     }
  }

}
