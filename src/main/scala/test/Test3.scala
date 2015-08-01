package test

/**
 * @author dk
 */
object Test3 {
  def main(args: Array[String]): Unit = {
    val s="http://tieba.baidu.com//f?kw=%CE%F7%B0%B2%B5%E7%D7%D3%BF%C6%BC%BC%B4%F3%D1%A7&fr=ala0&loc=rec"
    val reg="""(http://[^/]+/)""".r
    reg.findFirstIn(s) match {
      case Some(res)=>println(res)
      case None=>println("")
    }
  }
}