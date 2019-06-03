package forum

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.server.Directives._
import scala.language.implicitConversions
import com.typesafe.config.ConfigFactory
import scala.language.postfixOps
import spray.json._
import scala.util.{Failure, Success}
import akka.http.scaladsl.model.StatusCodes._
class Routes extends TopicsService with AnswersService with Protocols {
  val LIMIT = ConfigFactory.load().getInt("page.limit")

  val route =
    pathPrefix("topics") {
      get {
        (path(IntNumber) | parameters('id.as[Int])) { id =>
          complete(findTopic(id).map[ToResponseMarshallable] {
            case Some(t) => t
            case _ => ErrorMessage(s"Couldn't find topic with id $id")
          })
        } ~
        (parameters('page.as[Int].?, 'limit.as[Int].?)) { (page, limit) =>
          val p = page match {
            case Some(s) if s >= 0 => s
            case _ => 0
          }
          val l = limit match {
            case Some(s) if s >= 0 && s < LIMIT => s
            case _ => LIMIT
          }
          complete(findTopics(p, l))
        }
      } ~
      post { 
          entity(as[TopicInput]) { t =>
            val createTopicFuture = createTopic(t)
            onComplete(createTopicFuture) {
              case Success(s) => complete(SuccessMessage(s toString))
              case Failure(e) => complete(ErrorMessage("Unable to add topic"))
            }
          }
        } ~
      put {
        entity(as[UpdateRequest]){ a =>
          val updateTopicFuture = updateTopic(a)
          onComplete(updateTopicFuture) {
            case Success(s) => complete(ErrorMessage(s toString))
            case Failure(e) => complete(ErrorMessage("Unable to edit answer"))
          }
        }
      } ~
      delete {
        entity(as[DeleteRequest]){ a =>
          val deleteTopicFuture = deleteTopic(a)
          onComplete(deleteTopicFuture) {
            case Success(s) => complete(OK)
            case Failure(e) => complete(ErrorMessage("Unable to edit answer"))
          }
        }
     } 
    }~
      pathPrefix("answers") {
      get {
        (path(IntNumber) | parameter('id.as[Int])) { id =>
            complete(
              findAnswer(id).map[ToResponseMarshallable]{
                case Some(s) => s
                case None => (NotFound, ErrorMessage(s"answer with id $id not found"))
              }
            )
          }
        }
      } ~
      post { 
          entity(as[AnswerInput]) { a =>
            val createTopicFuture = createAnswer(a)
            onComplete(createTopicFuture) {
              case Success(s) => complete(SuccessMessage(s toString))
              case _ => complete(ErrorMessage("Unable to add topic"))
            }
          }
        } ~ 
      put {
        entity(as[UpdateRequest]){ a =>
          val updateAnswerFuture = updateAnswer(a)
          onComplete(updateAnswerFuture) {
            case Success(s) => complete(ErrorMessage(s toString))
            case Failure(e) => complete(ErrorMessage("Unable to edit answer"))
          }
        }
      } ~
      delete {
        entity(as[DeleteRequest]){ a =>
          val deleteAnswerFuture = deleteAnswer(a)
          onComplete(deleteAnswerFuture) {
            case Success(s) => complete(OK)
            case Failure(e) => complete(ErrorMessage("Unable to edit answer"))
          }
        }
  } ~
  redirect("/topics", PermanentRedirect)
} 

