package forum

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.pattern.ask
import akka.http.scaladsl.Http
import Database._
import akka.http.scaladsl.server.Directives._
import akka.util.Timeout
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import scala.concurrent.duration._
import scala.language.postfixOps

trait Routes extends ForumJsonProtocol {
    implicit val timeout = Timeout(2 seconds)
    val route = 
    pathPrefix("topics") {
        (path(IntNumber) | parameter('id.as[Int])) {topicID =>
            get {
                val topicFuture = (db ? GetTopic(topicID)).mapTo[Topic]
                    complete(topicFuture)
            } ~
            post {
                entity(as[Answer]) { answer =>
                    complete((db ? AddAnswer(answer)).map(_ => StatusCodes.OK)) 
                }
            } ~
            delete {
                entity(as[Answer]) { answer =>
                    complete((db ? DeleteAnswer(answer)).map(_ => StatusCodes.OK)) 
                }
            }
        } ~ 
        pathEndOrSingleSlash {
            get {
                val topicsFuture = (db ? GetAllTopics).mapTo[List[Topic]]
                complete(topicsFuture)        
            } ~
            post {
                entity(as[Topic]) { topic =>
                    complete((db ? AddTopic(topic)).map(_ => StatusCodes.OK)) 
                }
            } ~
            delete {
                entity(as[Topic]) { topic =>
                    complete((db ? DeleteTopic(topic)).map(_ => StatusCodes.OK)) 
                }
            }
        }
    }
}

