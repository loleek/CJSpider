package core

import java.nio.charset.Charset
import scala.concurrent.duration.DurationInt
import Messages.QueryStat
import Messages.ShutDown
import Messages.TaskStat
import Messages.GetData
import akka.actor.Actor
import akka.actor.ActorSelection.toScala
import akka.actor.actorRef2Scala
import akka.pattern.ask
import akka.util.Timeout
import akka.util.Timeout.durationToTimeout
import spray.can.Http
import spray.http.ContentType.apply
import spray.http.HttpEntity
import spray.http.HttpEntity.apply
import spray.http.HttpMethods.GET
import spray.http.HttpRequest
import spray.http.HttpResponse
import spray.http.MediaTypes._
import spray.http.Uri
import core.Messages.TaskStat

/**
 * @author dk
 */
class SpiderService extends Actor {

  implicit val timeout: Timeout = 5 seconds
  import context.dispatcher

  def receive = {

    case _: Http.Connected => sender ! Http.Register(self)

    case HttpRequest(GET, Uri.Path("/"), _, _, _) => {
      sender ! index
    }

    case HttpRequest(GET, Uri.Path("/crawlerList"), _, _, _) => {
      val client = sender
      context.actorSelection("/user/controller-actor") ? QueryStat onSuccess {
        case result: Set[_] => {
          client ! statsPresentation(result.asInstanceOf[Set[TaskStat]])
        }
        case result: String => {
          client ! HttpResponse(entity = (s"${result}"))
        }
      }
    }

    case HttpRequest(GET, Uri.Path("/data"), _, _, _) => {
      val client = sender
      context.actorSelection("/user/controller-actor") ? GetData onSuccess {
        case data: List[_] => {
          client ! generateDataPage(data.asInstanceOf[List[String]])
        }
        case result: String => {
          client ! HttpResponse(entity = (s"${result}"))
        }
      }
    }

    case HttpRequest(GET, Uri.Path("/shutdown"), _, _, _) => {
      sender ! HttpResponse(entity = "the spider system will be shurdown ...")
      context.actorSelection("/user/controller-actor") ! ShutDown
    }

  }

  lazy val index = HttpResponse(
    entity = HttpEntity(`text/html`,
      <html>
        <meta charset="UTF-8"/>
        <body>
          <h1>爬虫控制台</h1>
          <ul>
            <li><a href="/crawlerList">查看抓取列表</a></li>
            <li><a href="/data">查看所有数据</a></li>
          </ul>
          <br/>
          <br/>
          <ul>
            <li><a href="/shutdown">关闭爬虫</a></li>
          </ul>
        </body>
      </html>.toString().getBytes(Charset.forName("UTF-8"))))

  def statsPresentation(res: Set[TaskStat]) = HttpResponse(
    entity = HttpEntity(`text/html`,
      <html>
        <meta charset="UTF-8"/>
        <body>
          <h1>抓取任务状态</h1>
          <table>
            { res.map { stat => <tr><td>{ stat.name }</td><td>{ stat.url }</td><td>{ stat.stat }</td></tr> } }
          </table>
        </body>
      </html>.toString().getBytes(Charset.forName("UTF-8"))))

  def generateDataPage(list: List[String]) = HttpResponse(
    entity = HttpEntity(`text/html`,
      <html>
        <meta charset="UTF-8"/>
        <body>
          <h1>数据展示</h1>
          <table>
            {
              list.filter { line => line.split("\t").size == 3 }.map { line =>
                val args = line.split("\t")
                <tr><td>{ args(0) }</td><td><a href={ s"${args(1)}" }>{ args(2) }</a></td></tr>
              }
            }
          </table>
        </body>
      </html>.toString().getBytes(Charset.forName("UTF-8"))))

}