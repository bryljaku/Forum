import akka.http.scaladsl.model._
import org.scalatest.{FlatSpec, Matchers, WordSpec}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import RouteSpecHelper._
import akka.http.scaladsl.server._
import forum.models.{Answer, Protocols, Secret, Topic}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration


class RouteSpec
  extends FlatSpec
    with Matchers
    with ScalatestRouteTest
    with Protocols {

  def waitForIt[A](f: Future[A]): A = Await.result(f, Duration.Inf)
  val topic = Topic.from(topicValid)
  val topicSecret = topic.secret
  val topicId = topic.id
  val to = topicId
  waitForIt(db.run(topicsRepository.addTopic(topic)))

  val answer = Answer.from(answerValid, topicId)
  val answerId = answer.id
  val answerSecret = answer.secret
  waitForIt(db.run(answersRepository.addAnswer(answer)))

  for (i <- 1 to 4)
    waitForIt(db.run(answersRepository.addAnswer(Answer.from(answerValid, topicId))))



  "topics" should "respond with status OK when adding valid topic" in {
    Post("/topics", topicEntity(topicValid)) ~> Route.seal(route) ~> check {
      status shouldBe OK
    }
  }

  "topics" should "respond with status BadRequest when adding invalid topic" in {
    Post("/topics", topicEntity(topicInvalid)) ~> Route.seal(route) ~> check {
      status shouldBe BadRequest
    }
  }
  "topics" should "respond with status OK when getting topics list" in {
    Get("/topics") ~> route ~> check {
      status shouldBe OK
    }
  }
  "topics" should "respond with 404 Error when getting topic which does not exist" in {
    Get(s"/topics/$wrongUUID") ~> route ~> check {
      status shouldBe NotFound
    }
  }

  "topics" should "respond with status OK when updating topic" in {
    Put(s"/topics/$topicId", updateEntity(updateRequestValid(topicId, topicSecret))) ~> route ~> check {
      status shouldBe OK
    }
  }
  "topics" should "respond with status Unauthorized when updating topic with wrong secret" in {
    Put(s"/topics/$topicId", updateEntity(updateRequestValid(topicId, Secret(1)))) ~> route ~> check {
      status shouldBe BadRequest
    }
  }
  "answers" should "respond status OK when adding valid answer" in {
    Post(s"/topics/${topicId}/answers", answerEntity(answerValid)) ~> route ~> check {
      status shouldBe OK
    }
  }
  "answers" should "respond with status BadRequest when adding invalid answer" in {
    Post(s"/topics/${topicId}/answers", answerEntity(answerInvalid)) ~> route ~> check {
      status shouldBe BadRequest
    }
  }

  "answers" should "respond with status OK when getting answers" in
    Get(s"/topics/$topicId/answers") ~> route ~> check {
      status shouldBe OK
    }
  "answers" should "respond with correct pagination when getting answers with before > mid" in {
    Get(s"/topics/$topicId/answers?mid=1&before=2&after=1") ~> route ~> check {
      responseAs[Seq[Answer]].length shouldBe 3
    }
  }

  "answers" should "respond with status OK when updating answer" in {
    Put(s"/topics/$topicId/answers", updateEntity(updateRequestValid(answerId, answerSecret))) ~> route ~> check {
      status shouldBe OK
    }

  }
  "answers" should "respond with status Unauthorized when updating answer with wrong secret" in {
    Delete(s"/topics/$topicId/answers", updateEntity(updateRequestValid(answerId, Secret(1)))) ~> route ~> check {
      status shouldBe BadRequest
    }
  }
  "answers" should "respond with status Unauthorized when deleting answer with wrong secret" in {
    Delete(s"/topics/$topicId/answers", deleteEntity(deleteRequest(answerId, Secret(1)))) ~> route ~> check {
      status shouldBe BadRequest
    }
  }
  "answers" should "respond with status OK when deleting answer" in {
    Delete(s"/topics/$topicId/answers", deleteEntity(deleteRequest(answerId, answerSecret))) ~> route ~> check {
      status shouldBe OK
    }
  }

  "topics" should "respond with status Unauthorized when deleting topic with wrong secret" in {
    Delete(s"/topics/$topicId", deleteEntity(deleteRequest(topicId, Secret(1)))) ~> route ~> check {
      status shouldBe BadRequest
    }
  }
  "topics" should "respond with status OK when deleting topic" in {
    Delete(s"/topics/$topicId", deleteEntity(deleteRequest(topicId, topicSecret))) ~> route ~> check {
      status shouldBe OK
    }
  }

}
