package core

import akka.actor.ActorSystem
import akka.actor.Props
import spray.can.Http
import akka.io.IO

/**
 * @author dk
 */
object Main {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("cjspider-system")
    val contronlleractor = system.actorOf(Props[ControllerActor], "controller-actor")
    val handler = system.actorOf(Props[SpiderService], "handler")
    
    IO(Http) ! Http.Bind(handler, interface = "localhost", port = 23333)
  }
}