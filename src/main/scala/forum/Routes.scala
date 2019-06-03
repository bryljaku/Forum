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
      pathEndOrSingleSlash {
        get {
          (parameters('page.as[Int].?, 'limit.as[Int].?)) { (page, limit) =>
            val p = page match {
              case Some(s) if s >= 0 => s
              case _                 => 0
            }
            val l = limit match {
              case Some(s) if s >= 0 && s < LIMIT => s
              case _                              => LIMIT
            }
            complete(findTopics(p, l))
          }
        } ~
          post {
            entity(as[TopicInput]) { t =>
              complete(createTopic(t).map[ToResponseMarshallable] {
                case secret: Int if secret > 999 && secret < 10000 =>
                  SuccessMessage(
                    s"Topic added correctly. Secret: ${secret toString}")
                case _ => ErrorMessage("Unable to add topic")
              })
            }
          }
      } ~
        pathPrefix(IntNumber) { topicId =>
          pathPrefix("answers") {
            get {
              (path(IntNumber) | parameter('answerId.as[Int])) { answerId =>
                complete(findTopicAnswers(topicId, answerId)
                  .map[ToResponseMarshallable] {
                    case t: List[Answer] => t
                    case _ =>
                      ErrorMessage(s"Couldn't find topic with id $topicId")
                  })
              }
            } ~
              post {
                entity(as[AnswerInput]) { a =>
                  complete(createAnswer(a).map[ToResponseMarshallable] {
                    case secret: Int if secret > 999 && secret < 10000 =>
                      SuccessMessage(
                        s"Answer added correctly. Secret: ${secret toString}")
                    case _ => ErrorMessage("Unable to add answer")
                  })
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
