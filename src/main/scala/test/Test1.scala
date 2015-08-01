package test

import scala.io.Source
import java.io.File
import scala.util.matching.Regex
import org.apache.http.impl.client.HttpClients
import core.SpiderUtil

/**
 * @author dk
 */
object Test1 {
  def main(args: Array[String]): Unit = {
    val lines=Source.fromFile("rules"+File.separatorChar+"保密办公室").getLines()
    val url=lines.next()
    val rule=lines.next()
    
    val regex=new Regex(rule)
    val client=HttpClients.createDefault()
    
    val content=SpiderUtil.getContent(client, url)
    
    val it=regex.findAllIn(content)
    it.foreach { value => println(value) }
  }
}