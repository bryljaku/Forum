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
                    case Success((id,secret)) => complete(Created, (SuccessMessage.create, id, secret))
                    case Failure(ex) => complete(ex.getMessage)
                }
                case None => complete(BadRequest, ErrorMessage(ErrorMessage.wrongInput))
              })
            }
          }
      } ~
        pathPrefix(IntNumber) { topicId =>
          pathPrefix("answers") {
            get {                                   
                parameters('mid ? 0, 'before ? 0, 'after ? 20) { (mid, before, after) =>
                complete(findTopicAnswers(topicId, mid, before, after)
                  .map[ToResponseMarshallable] {
                    case t: List[Answer] => t
                    case _ => (NotFound, ErrorMessage(ErrorMessage.findAnswers)) 
                  })
              }
            } ~
              post {
                entity(as[AnswerInput]) { a =>
                  createAnswer(a) match {
                    case Some(resp) => onComplete(resp) {
                      case Success((id, secret)) if secret > 0 => complete((SuccessMessage.create, id, secret))
                      case Failure(ex) => complete(ex.getMessage)
                    }
                    case _ => complete(BadRequest, ErrorMessage(ErrorMessage.wrongInput))
                  }
                }
              } ~
             put {
                  entity(as[UpdateRequest]) { a =>
                    (updateAnswer(a) match {
                      case Some(dbAction) => 
                        onComplete(dbAction) {
                          case Success(1) => complete(SuccessMessage.update)
                          case Success(_) => complete(ErrorMessage.wrongInput)
                          case Failure(ex) => complete(ex.getMessage)
                        }
                      case None => complete(BadRequest, ErrorMessage(ErrorMessage.wrongUpdate))
                  })
                  }
                } ~
              delete {
                entity(as[DeleteRequest]) { a =>
                  complete(deleteAnswer(a).map[ToResponseMarshallable] {
                    case 1 => SuccessMessage(SuccessMessage.delete)
                    case _ => (Unauthorized, ErrorMessage(ErrorMessage.delete))
                  })
                }
              }
          } ~
            pathEndOrSingleSlash {
              get {
                complete(findTopic(topicId).map[ToResponseMarshallable] {
                  case Some(t) => t
                  case _ => (NotFound, ErrorMessage(ErrorMessage.findTopic + topicId))
                })
              } ~
                put {
                  entity(as[UpdateRequest]) { t =>
                    (updateTopic(t) match {
                      case Some(dbAction) => 
                        onComplete(dbAction) {
                          case Success(1) => complete(SuccessMessage.update)
                          case Success(_) => complete(ErrorMessage.wrongInput)
                          case Failure(ex) => complete(ex.getMessage)
                        }
                      case None => complete(BadRequest, ErrorMessage(ErrorMessage.wrongUpdate))
                  })
                  }
                } ~
                delete {
                  entity(as[DeleteRequest]) { t =>
                    complete(deleteTopic(t).map[ToResponseMarshallable] {
                      case 1 => SuccessMessage(SuccessMessage.delete)
                      case _ => (Unauthorized, ErrorMessage(ErrorMessage.delete))
                    })
                  }
                }
            }
        }
    } ~
    complete(NotFound)
}
