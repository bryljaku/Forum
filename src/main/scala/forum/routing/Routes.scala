package forum.routes

import java.util.UUID

import forum.models._
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import forum.routing.Protocols
import slick.jdbc.PostgresProfile.api._
import forum.services._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.{implicitConversions, postfixOps}

class Routes(db: Database, topicsService: TopicsService, answersService: AnswersService) extends Protocols {
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
                  complete{
                    answersService.findTopicAnswers(topicId, mid, before, after)
                      .map[ToResponseMarshallable] {
                      case Right(s) => OK -> s
                      case Left(e) => NotFound -> e
                    }
                  }
              }
            } ~
              post {
                entity(as[AnswerInput]) { a =>
                  complete {
                    answersService.createAnswer(a, topicId).map[ToResponseMarshallable] {
                      case Right(x) => x
                      case Left(e) => BadRequest -> e
                    }
                  }
                }
              } ~
              put {
                entity(as[UpdateRequest]) { a =>
                  complete {
                    answersService.updateAnswer(a).map[ToResponseMarshallable] {
                      case Right(s) => OK -> s
                      case Left(e) => BadRequest -> e
                    }
                  }
                }
              } ~
              delete {
                entity(as[DeleteRequest]) { a =>
                  complete {
                    answersService.deleteAnswer(a).map[ToResponseMarshallable] {
                      case Right(s) => OK -> s
                      case Left(e) => BadRequest -> e
                    }
                  }
                }
              }
          } ~
            pathEndOrSingleSlash {
              get {
                complete(topicsService.findTopic(topicId).map[ToResponseMarshallable] {
                  case Right(t) => OK -> t
                  case Left(e) => NotFound -> e
                })
              } ~
                put {
                  entity(as[UpdateRequest]) { t =>
                    complete {
                      topicsService.updateTopic(t).map[ToResponseMarshallable] {
                        case Right(s) => OK -> s
                        case Left(e) => BadRequest -> e
                      }
                    }
                  }
                } ~
                delete {
                  entity(as[DeleteRequest]) { t =>
                    complete {
                      topicsService.deleteTopic(t).map[ToResponseMarshallable] {
                        case Right(s) => OK -> s
                        case Left(e) => BadRequest -> e
                      }
                    }
                  }
                }
            }
        }
    }
}
