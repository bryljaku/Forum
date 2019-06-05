package forum

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.server.Directives._
import scala.language.implicitConversions
import scala.language.postfixOps
import spray.json._
import scala.util.{Failure, Success}
import akka.http.scaladsl.model.StatusCodes._
import slick.jdbc.H2Profile.api.Database

import AnswersService._
import TopicsService._

class Routes extends Protocols {
  val db: Database = Database.forConfig("postgres")

  val route =
    pathPrefix("topics") {
      pathEndOrSingleSlash {
        get {
          (parameters('page.as[Int].?, 'limit.as[Int].?)) { (page, limit) =>
            complete(findTopics(page, limit))
          }
        } ~
          post {
            entity(as[TopicInput]) { t =>
              (createTopic(t) match {
                case Some(dbAction) => 
                  onComplete(dbAction) {
                    case Success(response) => complete(SuccessMessage(s"secret: $response"))
                    case Failure(ex) => complete(ex.getMessage)
                }
                case None => complete(ErrorMessage("Something is wrong with your topic input"))
              })
            }
          }
      } ~
        pathPrefix(IntNumber) { topicId =>
          pathPrefix("answers") {
            get {                                   //  ANSWERSLIMIT
                parameters('mid ? 0, 'before ? 0, 'after ? 20) { (mid, before, after) =>
                complete(findTopicAnswers(topicId, mid, before, after)
                  .map[ToResponseMarshallable] {
                    case t: List[Answer] if t.nonEmpty => t
                    case _ =>
                      ErrorMessage(s"Couldn't find answers for topic with id: $topicId.")
                  })
              }
            } ~
              post {
                entity(as[AnswerInput]) { a =>
                  createAnswer(a) match {
                    case Some(resp) => onComplete(resp) {
                      case Success(secret) if secret > 999 && secret < 10000 => complete(SuccessMessage(s"secret: $secret"))
                      case Failure(ex) => complete(ex.getMessage)
                    }
                    case _ => complete(ErrorMessage("Check your answer input, something is wrong"))
                  }
                }
              } ~
              put {
                entity(as[UpdateRequest]) { a =>
                  complete(updateAnswer(a).map[ToResponseMarshallable] {
                    case 1 => SuccessMessage("answer updated successfully")
                    case _ => ErrorMessage("unable to update message")
                  })
                }
              } ~
              delete {
                entity(as[DeleteRequest]) { a =>
                  complete(deleteAnswer(a).map[ToResponseMarshallable] {
                    case 1 => SuccessMessage("answer deleted successfully")
                    case _ => ErrorMessage("unable to delete answer")
                  })
                }
              }
          } ~
            pathEndOrSingleSlash {
              get {
                complete(findTopic(topicId).map[ToResponseMarshallable] {
                  case Some(t) => t
                  case _ =>
                    ErrorMessage(s"Couldn't find topic with id $topicId")
                })
              } ~
                put {
                  entity(as[UpdateRequest]) { t =>
                    complete(updateTopic(t).map[ToResponseMarshallable] {
                      case 1 => SuccessMessage("topic updated successfully")
                      case _ => ErrorMessage("unable to update topic")
                    })
                  }
                } ~
                delete {
                  entity(as[DeleteRequest]) { t =>
                    complete(deleteTopic(t).map[ToResponseMarshallable] {
                      case 1 => SuccessMessage("topic deleted successfully")
                      case _ => ErrorMessage("unable to delete topic")
                    })
                  }
                }
            }
        }
    } 
      // redirect("/topics", PermanentRedirect)

}
