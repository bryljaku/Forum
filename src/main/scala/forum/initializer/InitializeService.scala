package forum

import slick.jdbc.PostgresProfile.api._
import slick.jdbc.meta.MTable
import forum.models._
import forum.repositories.{AnswersRepository, TopicsRepository}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success}

class InitializeService(db: Database, topicsRepository: TopicsRepository, answersRepository: AnswersRepository) extends Protocols{
  def startDB = {
    val dropFuture = Future {
      db.run(DBIO.seq(answersRepository.answersTable.schema.drop, topicsRepository.topicsTable.schema.drop))
    }
    Await.result(dropFuture, Duration.Inf).andThen {
      case Success(_) => {
        println("Initialization: Old tables dropped")
        createTablesAndInsert
      }
      case Failure(ex) => {
        println(s"drop failed, exception: $ex")
        createTablesAndInsert
      }
    }
  }

  def createTablesAndInsert = {
    val existing = db.run(MTable.getTables)
    val tables = List(topicsRepository.topicsTable, answersRepository.answersTable)

    val setupFuture = Future {
      existing.flatMap(v => {
        val names = v.map(mt => mt.name.name)
        val createIfNotExist = tables.filter(table =>
          !names.contains(table.baseTableRow.tableName)).map(_.schema.create)

        println(s"created tables $createIfNotExist")
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
      db.run(DBIO.seq(topicsRepository.topicsTable ++= addTopics))
    }
    Await.result(queryFuture, Duration.Inf).andThen {
      case Success(_) => println("Initialization: Topics added")
      case Failure(err) => println(err);
    }
  }

  def addTopics: Seq[Topic] =
    for (i <- 0 to 10)
      yield Topic.from(TopicInput(Nickname("topicNick" + i), Mail("topicMail" + i + "@e.do"), TopicName("Tooopic"), Content("Did you..")))
}