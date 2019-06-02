package forum

import slick.jdbc.H2Profile.api.Database
import slick.jdbc.PostgresProfile.api._
import java.sql.Timestamp
import scala.math.floor
import scala.util.Random.nextInt
import scala.language.postfixOps
import scala.concurrent. {
  Future,
  Await
}
import slick.jdbc.meta.MTable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.util. {
  Failure,
  Success
}
object DbStart extends DbBase with Protocols {

  lazy val dropCmd = DBIO.seq(topicsTable.schema.drop, answersTable.schema.drop)
  lazy val setup = DBIO.seq(topicsTable.schema.create, answersTable.schema.create, topicsTable ++= addTopics, answersTable ++= addAnswers)

  def addTopics: Seq[Topic] =
    for (i <- 0 to 10) 
     yield Topic(None, "jakisnick" + i, "Tooopic", "topicc", new Timestamp(nextInt), i toInt)

  def addAnswers: Seq[Answer] =
    for (i <- 1 to 30)
     yield Answer(None, "jakismail" + i + "@e.o", floor(i / 3) toInt, "answerstawe", new Timestamp(nextInt), i toInt)


  def dropDB = {

    val dropFuture = Future {
      println("to ja")
      db.run(dropCmd)
    }
    Await.result(dropFuture, Duration.Inf).andThen {
      case Success(_) => doSomething
      case Failure(_) => {
        println('s')
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
      db.run(answersTable.result).map(_.foreach {
        case x: Topic => println(s"${x}")
        case y: Answer => println(y)
      })
    }
    Await.result(queryFuture, Duration.Inf).andThen {
      case Success(_) => db.close()
      case Failure(err) => println(err);
      println("Oh Noes!")
    }
  }


  def startDB: Unit = {
    dropDB
  }
}