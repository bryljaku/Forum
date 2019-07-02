//package forum
//
//import slick.jdbc.PostgresProfile.api._
//import slick.jdbc.meta.MTable
//
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.duration.Duration
//import scala.concurrent.{Await, Future}
//import scala.language.postfixOps
//import scala.math.floor
//import scala.util.{Failure, Success}
//object InitializeService extends BaseService with InputHandler {
//  def startDB = {
//    val dropFuture = Future {
//      db.run(DBIO.seq(answersTable.schema.drop, topicsTable.schema.drop))
//    }
//    Await.result(dropFuture, Duration.Inf).andThen {
//      case Success(_) => {
//        println("Initialization: Old tables dropped")
//        createTablesAndInsert
//      }
//      case Failure(ex) => {
//        println(s"drop failed, exception: $ex")
//        createTablesAndInsert
//      }
//    }
//  }
//
//  def createTablesAndInsert = {
//    val existing = db.run(MTable.getTables)
//    val tables = List(topicsTable, answersTable)
//
//    val setupFuture = Future {
//      existing.flatMap(v => {
//        val names = v.map(mt => mt.name.name)
//        val createIfNotExist = tables.filter(table =>
//          !names.contains(table.baseTableRow.tableName)).map(_.schema.create)
//        db.run(DBIO.sequence(createIfNotExist))
//      })
//    }
//    Await.result(setupFuture, Duration.Inf).andThen {
//      case Success(_) => runQuery
//      case Failure(err) => println(err);
//    }
//  }
//
//  def runQuery = {
//    val queryFuture = Future {
//      db.run(DBIO.seq(topicsTable ++= addTopics, answersTable ++= addAnswers))
//    }
//    Await.result(queryFuture, Duration.Inf).andThen {
//      case Success(_) => println("Initialization: Topics and answers added")
//      case Failure(err) => println(err);
//    }
//  }
//
//  def addTopics: Seq[Topic] =
//    for (i <- 0 to 10)
//      yield topicFromInput(TopicInput(Nickname("topicNick" + i), Mail("topicMail" + i + "@e.o"), TopicName("Tooopic"), Content("Did you..")))
//
//  def addAnswers: Seq[Answer] =
//    for (i <- 0 to 30)
//      yield answerFromInput(AnswerInput(Nickname("answerNick"), Mail("answerMail" + i + "@e.o"), Content("Yes, I...")),TopicID(floor(i / 3 + 1) toString))
//
//}