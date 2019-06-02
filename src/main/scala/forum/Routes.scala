package forum
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import scala.concurrent.ExecutionContext.Implicits.global
import akka.http.scaladsl.server.Directives._
import com.typesafe.config.ConfigFactory

import scala.language.postfixOps



import java.sql.Timestamp
import java.util.Date
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import scala.language.implicitConversions

class Routes extends DbOperations {
  val LIMIT = ConfigFactory.load().getInt("page.limit")
  val route =
    pathPrefix("topics") {
      get {
        (path(IntNumber) | parameters('id.as[Int])) { id =>
          complete(findTopic(id).map[ToResponseMarshallable] {
            case Some(t) => "ss"
            case _ => "cos jestnie tak"
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
          reject
          // entity(as[TopicInput]) { t =>
          //   complete(createTopic(t).map[ToResponseMarshallable])
          }
        } 
    //     ~
    //   put {
    //     entity(as[UpdateRequest]){ t =>
    //       complete(updateTopic(t).map[ToResponseMarshallable])
    //     }
    //   } ~
    //   delete {
    //     entity(as[DeleteRequest]){ t =>
    //       complete(deleteTopic(t).map[ToResponseMarshallable])
    //     }
    //   }
    // } ~
    //   pathPrefix("answers") {
    //   get {
    //     (path(IntNumber) | parameter('id.as[Int])) { id =>
    //         complete(findAnswer(id).mapTo[Int])
    //       }
    //     }
    //   } ~
    //   post { 
    //       entity(as[AnswerInput]) { a =>
    //         complete(createTopic(a).map[ToResponseMarshallable])
    //       }
    //     } ~
    //   put {
    //     entity(as[UpdateRequest]){ a =>
    //       complete(updateTopic(a).map[ToResponseMarshallable])
    //     }
    //   } ~
    //   delete {
    //     entity(as[DeleteRequest]){ a =>
    //       complete(deleteTopic(a).map[ToResponseMarshallable])
    //     }
  // }
} 






