package forum

import java.util.UUID

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import forum.repositories.{AnswersRepository, TopicsRepository}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.{implicitConversions, postfixOps}
import scala.util.{Failure, Success}

class Routes(db: Database) extends Protocols {
  val topicsRepository = new TopicsRepository
  val answersRepository = new AnswersRepository
  val answersService = new AnswersService(db, answersRepository)
  val topicsService = new TopicsService(db, topicsRepository)

  val route =
    pathPrefix("topics") {
      pathEndOrSingleSlash {
        get {
          parameters('page.as[Int].?, 'limit.as[Int].?) { (page, limit) =>
            complete(topicsService.findTopics(page, limit))
          }
        } ~
          post {
            entity(as[TopicInput]) { t =>
              complete {
                topicsService.createTopic(t).map[ToResponseMarshallable] {
                  case Right(x) => x
                  case Left(e) => BadRequest -> e
                }
              }
            }
          }
      } ~
        pathPrefix(Segment) { x =>
          val topicId = Id(UUID.fromString(x))
          pathPrefix("answers") {
            get {
              parameters('mid ? 0, 'before ? 0, 'after ? 20) {
                (mid, before, after) =>
                  complete(answersService.findTopicAnswers(topicId, mid, before, after)
                    .map[ToResponseMarshallable] {
                    case a: List[Answer] => a
                    case _ => NotFound -> ErrorMessage(ErrorMessage.findAnswers)
                  })
              }
            } ~
              post {
                entity(as[AnswerInput]) { a =>
                  answersService.createAnswer(a, topicId) match {
                    case Some(resp) =>
                      onComplete(resp) {
                        case Success((id, secret)) =>
                          complete(ContentCreatedMessage(SuccessMessage.create, id, secret))
                        case Failure(ex) => complete(ex.getMessage)
                      }
                    case _ => complete(BadRequest -> ErrorMessage(ErrorMessage.wrongInput))
                  }
                }
              } ~
              put {
                entity(as[UpdateRequest]) { a =>
                  answersService.updateAnswer(a) match {
                    case Some(dbAction) =>
                      onComplete(dbAction) {
                        case Success(1) => complete(SuccessMessage.update)
                        case Success(_) => complete(Unauthorized -> ErrorMessage.wrongInput)
                        case Failure(ex) => complete(ex.getMessage)
                      }
                    case None =>
                      complete(BadRequest -> ErrorMessage(ErrorMessage.wrongUpdate))
                  }
                }
              } ~
              delete {
                entity(as[DeleteRequest]) { a =>
                  complete(answersService.deleteAnswer(a).map[ToResponseMarshallable] {
                    case 1 => SuccessMessage(SuccessMessage.delete)
                    case _ => Unauthorized -> ErrorMessage(ErrorMessage.delete)
                  })
                }
              }
          } ~
            pathEndOrSingleSlash {
              get {
                complete(topicsService.findTopic(topicId).map[ToResponseMarshallable] {
                  case Some(t) => t
                  case _ => NotFound -> ErrorMessage(ErrorMessage.findTopic + topicId)
                })
              } ~
                put {
                  entity(as[UpdateRequest]) { t =>
                    topicsService.updateTopic(t) match {
                      case Some(dbAction) =>
                        onComplete(dbAction) {
                          case Success(1) => complete(SuccessMessage.update)
                          case Success(_) => complete(Unauthorized, ErrorMessage.wrongInput)
                          case Failure(ex) => complete(ex.getMessage)
                        }
                      case None =>
                        complete(BadRequest -> ErrorMessage(ErrorMessage.wrongUpdate))
                    }
                  }
                } ~
                delete {
                  entity(as[DeleteRequest]) { t =>
                    complete(topicsService.deleteTopic(t).map[ToResponseMarshallable] {
                      case 1 => SuccessMessage(SuccessMessage.delete)
                      case _ => Unauthorized -> ErrorMessage(ErrorMessage.delete)
                    })
                  }
                }
            }
        }
    }
}
