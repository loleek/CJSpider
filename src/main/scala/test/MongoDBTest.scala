package test
import com.mongodb.casbah.Imports._
/**
 * @author dk
 */
object MongoDBTest {
  def main(args: Array[String]): Unit = {
    val mongoClient=MongoClient("localhost",27017)
    mongoClient.getDatabaseNames().foreach { x => println(x) }
    mongoClient.close()
  }
}