package test

import core.SpiderUtil
import org.apache.http.impl.client.HttpClients
import scala.io.Source
import java.io.File
import org.jsoup.Jsoup
import scala.collection.mutable.ArrayBuffer

/**
 * @author dk
 */
object Test2 {
  def main(args: Array[String]): Unit = {
    
    val lines = Source.fromFile("rules" + File.separatorChar + "西电贴吧").getLines()
    //    val lines = Source.fromFile("rules" + File.separatorChar + "保卫处").getLines()
    val url = lines.next()
    val jsoupRules = ArrayBuffer.empty[String]
    val regexRules = ArrayBuffer.empty[String]

    lines.foreach { line =>
      val args = line.split(":=")
      args match {
        case Array("jsoup", value) => jsoupRules += value
        case Array("regex", value) => regexRules += value
        case _                     => println("wrong rule")
      }
    }

    val client = HttpClients.createDefault()

    val content = SpiderUtil.getContent(client, url)

    val result = ArrayBuffer.empty[String]

    val doc = Jsoup.parse(content)
    val elements = doc.getElementsByTag("a").iterator()
    while (elements.hasNext()) {
      val element = elements.next
      if (element.hasAttr("href")) {
        val href = element.attr("href")
        if (jsoupRules.forall { rule => href.contains(rule) } && element.text() != "") {
          if (href.startsWith("/")) {
            if (url.endsWith("/"))
              result += s"${url + href.substring(1)}\t${element.text()}"
            else
              result += s"${url + href}\t${element.text()}"
          } else {
            if (url.endsWith("/"))
              result += s"${url + href}\t${element.text()}"
            else
              result += s"${url + "/" + href}\t${element.text()}"
          }
        }
      }
    }

    result.foreach { x => println(x) }
  }
}