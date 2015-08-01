package core

import scala.collection.mutable.ArrayBuffer
import org.apache.http.impl.client.HttpClients
import Messages.Task
import akka.actor.Actor
import org.jsoup.Jsoup
import Messages.Result
import Messages.WrongResult
import java.net.URL
import java.io.IOException

/**
 * @author dk
 */
class SpiderActor extends Actor {

  var client = HttpClients.createDefault()

  def receive = {
    case Task(name, url, rulesFlag, jsoupRules, regexRules) => {
      val result = ArrayBuffer.empty[String]

      if (rulesFlag == 0) {
        try {
          val doc = Jsoup.parse(new URL(url), 5000)
          val elements = doc.getElementsByTag("a").iterator()

          while (elements.hasNext()) {
            val element = elements.next
            if (element.hasAttr("href")) {
              val href = element.attr("href")

              if (jsoupRules.exists { rule => href.contains(rule) }) {
                val finalurl = if (!href.startsWith("http")) {
                  val tmpurl = if (url.endsWith("/")) url else url + "/"
                  val reg = """(http://[^/]+/)""".r
                  val furl = reg.findFirstIn(tmpurl).get

                  if (href.startsWith("/")) {
                    s"${furl + href.substring(1)}"
                  } else {
                    s"${furl + href}"
                  }
                } else {
                  s"${href}"
                }
                val title = if (element.attr("title") != "") element.attr("title").trim else element.text.trim

                result += s"${finalurl}\t${title}"
              }
            }
          }
          sender ! Result(name, result)
        } catch {
          case ex: IOException => {
            sender ! WrongResult(name,url)
          }
        }

      } else {
        //        val content = SpiderUtil.getContent(client, url)
        //        use Regex to impl
      }
    }
  }
}