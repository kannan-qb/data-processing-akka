package com.qbrainx.model

import akka.actor.{Actor, ActorRef}
import com.qbrainx.model.Worker.{Execute, Result}
import com.qbrainx.service._

class Worker extends Actor with WorkHandler {
  override def receive: Receive = {
    case Execute(id, task, sender) =>
      sender ! Result(id, convertToLog(task))
  }
}
object Worker {
  case class Log(ip: String, time: Date, url: String, status: String)
  case class Date(year: Int, month: String, date: Int, time: String)
  case class Execute(taskId: Int, task: String, replyTo: ActorRef)
  case class Result(workerId: Int, result: Log)
}