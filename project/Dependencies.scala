import sbt._
object Dependencies {

  lazy val akkaVersion = "2.6.16"

  lazy val typeSafeConfigVersion = "1.4.1"


  lazy val akkaActor ="com.typesafe.akka" %% "akka-actor" % akkaVersion

  lazy val typeSafeConfig = "com.typesafe" % "config" % typeSafeConfigVersion

  def compileDependencies: Seq[ModuleID] = Seq(akkaActor,typeSafeConfig)

}
