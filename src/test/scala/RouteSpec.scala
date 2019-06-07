import akka.http.scaladsl.model._
import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.testkit.ScalatestRouteTest
import scala.collection._
import akka.util.ByteString
import forum.Server._

import akka.event.NoLogging
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.{HttpResponse, HttpRequest}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.stream.scaladsl.Flow
import org.scalatest._
import RouteSpecHelper._
// val topicInputEntity = TopicInput("user", "correct@ma.il", "subject", "content")
// val wrongTopicInputEntity = TopicInput("user", "wrongma.il", "subject", "content")
// val answerInputEntity = AnswerInput("user", "correct@ma.il", 1, "content")
// val wrongAnswerInputEntity = AnswerInput("user", "wrongma.il", 1, "content")

// val topicInput = ByteString( """{
//     "content": "topicc",
//     "nickname": "jakisnick5",
//     "mail": "topicowy@mail.com",
//     "topic": "Tooopic"
// }""".stripMargin)

class RouteSpec extends WordSpec with Matchers with ScalatestRouteTest {

"Service" should {
    "/topics return status ok" in {
        Get("/topics") ~> route ~> check {
            status shouldBe OK
        }
    }
    "/topics/id respond with 404 error" in {
        Get("/topics/123457") ~> route ~> check {
            status shouldBe NotFound
      }
    }
    "/answers/id respond status ok" in {
        Get("/topics/")
    }

    // "return id and secret when adding topic" in {
    //     Post("topics").withEntity(topicInputEntity) ~> route ~> check {
    //         responseAs[(String, Int, Int)]
    //     }
    // }
}
}