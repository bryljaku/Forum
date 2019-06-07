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
     yield Topic(None, "topicNick" + i, "topicMail" + i + "@e.o", "Tooopic", "Did you..", new Date, i toInt)

  def addAnswers: Seq[Answer] =
    for (i <- 0 to 30)
     yield Answer(None, "answerNick", "answerMail" + i + "@e.o", floor(i / 3 + 1) toInt, "Yes, I...", new Date, i toInt)


  def startDB = {
    val dropFuture = Future {
      db.run(DBIO.seq(answersTable.schema.drop, topicsTable.schema.drop))
    }
    Await.result(dropFuture, Duration.Inf).andThen {
      case Success(_) => createTablesAndInsert
      case Failure(ex) => {
        println(s"drop failed, exception: $ex")
        createTablesAndInsert
      }
    }
  }
  def createTablesAndInsert = {
      val existing = db.run(MTable.getTables)
      val tables = List(topicsTable, answersTable) 

    val setupFuture = Future {
    existing.flatMap( v => {
      val names = v.map(mt => mt.name.name)
      val createIfNotExist = tables.filter( table =>
        (!names.contains(table.baseTableRow.tableName))).map(_.schema.create)
      db.run(DBIO.sequence(createIfNotExist))
      })
    }
    Await.result(setupFuture, Duration.Inf).andThen {
      case Success(_) => runQuery
      case Failure(err) => println(err);
    }
  }
  def runQuery = {
    val queryFuture = Future {
        db.run(DBIO.seq(topicsTable ++= addTopics, answersTable ++= addAnswers))
    }
    Await.result(queryFuture, Duration.Inf).andThen {
      case Success(_) => println("querySuccess")
      case Failure(err) => println(err);
    }
  }

}