package core

import scala.collection.JavaConversions.bufferAsJavaList
import scala.collection.mutable.Buffer
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.message.BasicNameValuePair
import org.jsoup.Jsoup
import org.apache.http.client.config.RequestConfig
import java.io.IOException

/**
 * Created by dk on 2015/6/15.
 */
object SpiderUtil {

  def getContent(client: CloseableHttpClient, url: String): String = {
    val request = new HttpGet(url)
    request.setConfig(config)
    val response = client.execute(request)
    try {
      val in = response.getEntity.getContent

      var bytes = new Array[Byte](8192)
      var len = 1
      var s = ""
      while (len > 0) {
        len = in.read(bytes)
        if (len > 0)
          s = s + new String(bytes, 0, len)
      }
      s
    } catch {
      case e: IOException => throw e
    } finally {
      response.close()
    }
  }

  val config = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build()

}
