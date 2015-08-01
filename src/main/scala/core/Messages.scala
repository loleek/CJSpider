package core
import scala.collection.mutable.ArrayBuffer
/**
 * @author dk
 */
object Messages {
  case object GenerateTasks
  case class Task(name: String, url: String, rulesFlag: Int, jsoupRules: ArrayBuffer[String], regexRules: ArrayBuffer[String])
  case class Result(name: String, result: ArrayBuffer[String])
  case class WrongResult(name: String, url: String)
  case class TaskStat(name: String, url: String, stat: String)
  case object GetData
  case object QueryStat
  case object ShutDown
}