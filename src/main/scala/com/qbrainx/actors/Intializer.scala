package com.qbrainx.actors
import akka.actor._
import Intializer._
import com.qbrainx.actors.MasterActor.Aggregate
import com.qbrainx.service.Intiator

import scala.io.Source
class Intializer extends Actor with ActorLogging with Intiator {

  val inputStream = openInputStream
  var masterNode: ActorRef = context.actorOf(MasterActor.props(context.self), "masterNode")
  override def receive: Receive = {
    case StartIngestion(noofWorker) =>
      log.info("[Intializer] Initializing Worker ...")
      masterNode ! MasterActor.InitializeWorker(noofWorker)
    case MasterActor.WorkerInitialized =>
      log.info("[Intializer] worker is initialized. Getting lines from source and send to masterNode ...")
      Source.fromInputStream(inputStream).getLines().toList.filter(isValidIp).foreach(masterNode ! Data(_))
      inputStream.close()
    case Aggregate(result) =>
      log.info(s"[Intializer] total status Code: ${result.keys.map(k => s"$k -> ${result(k).length}").toString()}")
      val lines = result.keys.map { key =>
        s"Status : $key has a total of ${result(key).length} amount}"
      }

      writeToOutputFile(lines)
      context.parent ! Supervisor.Stop
  }
}

object Intializer {
  case class StartIngestion(noofWorker: Int)
  case class Data(logString: String)
}
