package com.qbrainx.application

import akka.actor.ActorSystem
import com.qbrainx.model.Supervisor

object Application extends App{
  val system = ActorSystem("DataProcessingAkka")
  val supervisorActor = system.actorOf(Supervisor.props(100), "supervisor")

  supervisorActor ! Supervisor.Start


}
