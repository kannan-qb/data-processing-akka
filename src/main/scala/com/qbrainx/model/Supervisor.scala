package com.qbrainx.model
import Supervisor._
import akka.actor._

class Supervisor(nWorker: Int) extends Actor with ActorLogging {

  override def receive: Receive = {
    case Start =>
      val ingestion = context.actorOf(Props[Intializer], "ingestion")
      ingestion ! Intializer.StartIngestion(nWorker)
    case Stop =>
      log.info("[Supervisor] All things are done, stopping the system")
      context.system.terminate()
  }
}
object Supervisor {
  case object Start
  case object Stop

  def props(m: Int) = Props(new Supervisor(m))
}