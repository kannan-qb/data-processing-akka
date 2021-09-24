package com.qbrainx.model

import akka.actor._
import com.qbrainx.model.Intializer._
import com.qbrainx.model.MasterActor._
import com.qbrainx.model.Worker._

class MasterActor(parent: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = preInitialize

  def preInitialize: Receive = {
    case InitializeWorker(nWorker) =>
      log.info(s"[Master] Initializing $nWorker worker(s)...")
      val workers = (0 to nWorker).toVector.map(id => context.actorOf(Props[Worker], s"${id}-worker"))
      sender() ! WorkerInitialized
      context.become(
        workerInitialized(
          currentWorkerId = 0,
          currentTaskId = 0,
          workers = workers,
          Set.empty[Int],
          resultMap = Map.empty[String, List[Log]]
        )
      )
  }

  def workerInitialized(
                         currentWorkerId: Int,
                         currentTaskId: Int,
                         workers: Vector[ActorRef],
                         taskIdSet: Set[Int],
                         resultMap: Map[String, List[Log]]
                       ): Receive = {
    case Data(logString) =>
      log.info(s"[Master] received $logString assigning taskId $currentTaskId to worker $currentWorkerId")
      val currentWorker = workers(currentWorkerId)
      val newTaskIdSet = taskIdSet + currentTaskId

      currentWorker ! Execute(currentTaskId, logString, context.self)

      val newTaskId = currentTaskId + 1
      val newWorkerId = (currentWorkerId + 1) % workers.length

      context.become(workerInitialized(newWorkerId, newTaskId, workers, newTaskIdSet, resultMap))

    case Worker.Result(id, result) =>
      log.info(s"[Master] Received result $result from taskId $id")
      val newTaskIdSet = taskIdSet - id

      val newResultMap = result match {
        case log @ Log(_, _, _, status) =>
          val logList = resultMap.getOrElse(status, List.empty[Log])
          resultMap + (status -> (log :: logList))
      }

      if (newTaskIdSet.isEmpty) {
        log.info(s"[Master] All task is done, sending result back to ${parent.path}")
        parent ! Aggregate(newResultMap)
      } else {
        log.info(s"[Master] Task is not yet all done, waiting for other workers to send back results")
        context.become(workerInitialized(currentWorkerId, currentTaskId, workers, newTaskIdSet, newResultMap))
      }
  }
}
object MasterActor {
    case class InitializeWorker(nWorker: Int)
    case object WorkerInitialized
    case class Aggregate(numberOfStatus: Map[String, List[Log]])

    def props(parent: ActorRef): Props = Props(new MasterActor(parent))
}