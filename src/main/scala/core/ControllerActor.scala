package core

import java.io.File
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._
import scala.io.Source
import com.mongodb.casbah.Imports._
import Messages.GenerateTasks
import Messages.Result
import Messages.ShutDown
import Messages.Task
import Messages.GetData
import akka.actor.Actor
import akka.actor.Props
import core.Messages.WrongResult
import scala.collection.mutable.HashMap
import core.Messages.QueryStat
import core.Messages.TaskStat

/**
 * @author dk
 */
class ControllerActor extends Actor {

  val spider = context.actorOf(Props[SpiderActor], "spider")

  import context.dispatcher
  context.system.scheduler.schedule(0 seconds, 1 hour, self, GenerateTasks)

  //  val mongoClient = MongoClient("localhost", 27017)
  //  val db = mongoClient("data")
  //  val collection = db("content")

  val namestat = new HashMap[String, String]()
  val nameurl = new HashMap[String, String]()
  val nameresult = new HashMap[String, List[String]]()

  var taskcount = 0

  def receive = {
    case GenerateTasks => {
      val directory = new File("rules")
      val filelist = directory.listFiles()
      taskcount = filelist.length

      filelist.foreach { file =>
        val lines = Source.fromFile(file).getLines()
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

        nameurl.put(file.getName, url)

        spider ! Task(file.getName, url, 0, jsoupRules, regexRules)
      }
    }
    case Result(name, result) => {
      //      val query = MongoDBObject("name" -> name)
      //      val update = $set("data" -> result.toList)
      //      val res = collection.update(query, update, upsert = true)

      nameresult.put(name, result.toList)
      namestat.put(name, "ok")
    }
    case WrongResult(name, url) => {
      namestat.put(name, "wrong")
    }
    case QueryStat => {
      if (namestat.size == taskcount) {
        val names = nameurl.keySet
        val result = names.map { name =>
          TaskStat(name, nameurl(name), namestat(name))
        }
        sender ! result
      } else {
        sender ! "task not finish , please wait ..."
      }
    }
    case GetData=>{
      if (namestat.size == taskcount) {
        val names=nameresult.keySet.toList
        val result=names.flatMap { name=>
          nameresult(name).map { line=>
            s"${name}\t${line}"
          }
        }

        sender ! result
      } else {
        sender ! "task not finish , please wait ..."
      }
    }
    case ShutDown => {
      //      mongoClient.close()
      context.system.shutdown()
    }
  }
}