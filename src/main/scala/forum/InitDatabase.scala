package forum

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import slick.jdbc.H2Profile.api.Database
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.{Future, Await}
import DateTimestampConversion._
import java.util.Date
import scala.util.Random.nextInt
import scala.util.{Failure, Success}
import scala.language.postfixOps
import slick.jdbc.meta.MTable
import java.sql.Timestamp
import scala.math.floor

object InitDatabase extends DbBase {
  def addTopics: Seq[Topic] =
    for (i <- 0 to 10) 
     yield Topic(None, "jakisnick" + i, "jakismail", "Tooopic", "topicc", new Date, i toInt)

  def addAnswers: Seq[Answer] =
    for (i <- 1 to 30)
     yield Answer(None, "jakisNick", "jakismail" + i + "@e.o", floor(i / 4 + 1) toInt, "answerstawe", new Date, i toInt)


  def startDB = {
    val dropFuture = Future {
      println("to ja")
      db.run(DBIO.seq(topicsTable.schema.drop))
    }
    Await.result(dropFuture, Duration.Inf).andThen {
      case Success(_) => doSomething
      case Failure(_) => {
        println("drop failed")
        doSomething
      }
    }
  }
  def doSomething = {
      val existing = db.run(MTable.getTables)
      val tables = List(topicsTable, answersTable) 

    val setupFuture = Future {
    existing.flatMap( v => {
      val names = v.map(mt => mt.name.name)
        println("tworze tabelki")

      val createIfNotExist = tables.filter( table =>
        (!names.contains(table.baseTableRow.tableName))).map(_.schema.create)
      db.run(DBIO.sequence(createIfNotExist))
      })
    }
    Await.result(setupFuture, Duration.Inf).andThen {
      case Success(_) => runQuery
      case Failure(err) => println(err);
    }
    println("Seeya!")
  }
  def runQuery = {
    val queryFuture = Future {
        db.run(DBIO.seq(answersTable ++= addAnswers))
        
    }
    Await.result(queryFuture, Duration.Inf).andThen {
      case Success(_) => println("querySuccess")
      case Failure(err) => println(err);
      println("Oh Noes!")
    }
  }

}