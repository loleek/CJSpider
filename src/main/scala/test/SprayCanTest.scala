package test

import java.nio.charset.Charset

import akka.actor.Actor
import akka.actor.ActorSystem
import akka.actor.Props
import akka.io.IO
import spray.can.Http
import spray.http._
import spray.http.HttpMethods._
import spray.http.HttpRequest
import spray.http.MediaTypes._

/**
 * @author dk
 */
object SprayCanTest {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("spraycan-test")

    val handler = system.actorOf(Props[TestService], "handler")
    IO(Http) ! Http.Bind(handler, interface = "localhost", port = 2333)
  }
}

class TestService extends Actor {
  def receive = {

    case _: Http.Connected => sender ! Http.Register(self)
    
    case HttpRequest(GET,Uri.Path("/"), _, _, _) => {
      sender ! index
    }
    
    case HttpRequest(GET,Uri.Path("crawlerList"),_,_,_)=>{
      
    }

  }

  lazy val index = HttpResponse(
    entity = HttpEntity(`text/html`,
      <html>
				<meta charset="UTF-8"/>
        <body>
          <h1>Crawler Console</h1>
          <ul>
            <li><a href="/crawlerList">查看抓取列表</a></li>
            <li><a href="/data">查看所有数据</a></li>
            <li><a href="/shutdown">关闭爬虫</a></li>
          </ul>
        </body>
      </html>.toString().getBytes(Charset.forName("UTF-8"))))
}