package test

import org.jsoup.Jsoup
import java.net.URL
/**
 * @author dk
 */
object Test4 {
  def main(args: Array[String]): Unit = {
    val doc=Jsoup.parse(new URL("http://gr.xidian.edu.cn/"),5000)
    println(doc.charset())
  }
}