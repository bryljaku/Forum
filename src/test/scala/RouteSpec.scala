import akka.http.scaladsl.model._
import org.scalatest.{Matchers, WordSpec}

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import RouteSpecHelper._
import akka.http.scaladsl.server._
import forum.models.{Answer, Protocols, Secret}


class RouteSpec
    extends WordSpec
    with Matchers
    with ScalatestRouteTest
    with Protocols
   {


  "Service" should {
    "topics" should {
      "respond with status OK when adding valid topic" in {
        Post("/topics").withEntity(topicEntity(topicValid)) ~> Route.seal(route) ~> check {
          status shouldBe Created
        }
      }
      "respond with status BadRequest when adding invalid topic" in {
        Post("/topics").withEntity(topicEntity(topicInvalid)) ~> Route.seal(route) ~> check {
          status shouldBe BadRequest
        }
      }
      "respond with status OK when getting topics list" in {
        Get("/topics") ~> route ~> check {
          status shouldBe OK
        }
      }
      "respond with 404 Error when getting topic which does not exist" in {
        Get("/topics/1234575") ~> route ~> check {
          status shouldBe NotFound
        }
      } 
      Post("/topics").withEntity(topicEntity(topicValid)) ~> Route.seal(route) ~> check {
        val topicResponse = toResponseMessage(response)
        val topicId = topicResponse.id
        val secret = topicResponse.secret
        "respond with status OK when updating topic" in {
          Put(s"/topics/$topicId").withEntity(updateEntity(
            updateRequestValid(topicId, secret))) ~> route ~> check {
            status shouldBe OK
          }
        }
        "respond with status Unauthorized when updating topic with wrong secret" in {
          Put(s"/topics/$topicId").withEntity(
            updateEntity(updateRequestValid(topicId, Secret(1)))) ~> route ~> check {
            status shouldBe Unauthorized
          }
        }
        "respond with status Unauthorized when deleting topic with wrong secret" in {
          Delete(s"/topics/$topicId").withEntity(
            deleteEntity(deleteRequest(topicId, Secret(1)))) ~> route ~> check {
            status shouldBe Unauthorized
          }
        }
        "respond with status OK when deleting topic" in {
          Delete(s"/topics/$topicId").withEntity(
            deleteEntity(deleteRequest(topicId, secret))) ~> route ~> check {
            status shouldBe OK
          }
        }
      }
    }
    "answers" should {
      Post("/topics").withEntity(topicEntity(topicValid)) ~> Route.seal(route) ~> check {
        val topicId = toResponseMessage(response).id
        "respond status OK when adding valid answer" in {
          Post(s"/topics/${topicId}/answers")
            .withEntity(answerEntity(answerValid(topicId))) ~> route ~> check {
            status shouldBe OK
          }
        }
        "respond with status BadRequest when adding invalid answer" in {
          Post(s"/topics/${topicId}/answers").withEntity(
            answerEntity(answerInvalid(topicId))) ~> route ~> check {
            status shouldBe BadRequest
          }
        }
        for (i <- 1 to 4)
          Post(s"/topics/$topicId/answers")
            .withEntity(answerEntity(answerValid(topicId))) ~> Route.seal(route)

        "respond with status OK when getting answers" in
          Get(s"/topics/$topicId/answers") ~> route ~> check {
            status shouldBe OK
          }
        "respond with correct pagination when getting answers with before > mid" in {
          Get(s"/topics/$topicId/answers?mid=1&before=2&after=1") ~> route ~> check {
            responseAs[List[Answer]].length shouldBe 3
          }
        }

        Post(s"/topics/$topicId/answers").withEntity(
          answerEntity(answerValid(topicId))) ~> Route.seal(route) ~> check {
          val answerResponse = toResponseMessage(response)
          val secret = answerResponse.secret
          val answerId = answerResponse.id

          "respond with status OK when updating answer" in {
            Put(s"/topics/$topicId/answers").withEntity(updateEntity(
              updateRequestValid(answerId, secret))) ~> route ~> check {
              status shouldBe OK
            }

          }
          "respond with status Unauthorized when updating answer with wrong secret" in {
            Delete(s"/topics/$topicId/answers").withEntity(updateEntity(
              updateRequestValid(answerId, Secret(1)))) ~> route ~> check {
              status shouldBe Unauthorized
            }
          }
          "respond with status Unauthorized when deleting answer with wrong secret" in {
            Delete(s"/topics/$topicId/answers").withEntity(
              deleteEntity(deleteRequest(answerId, Secret(1)))) ~> route ~> check {
              status shouldBe Unauthorized
          }
          }
          "respond with status OK when deleting answer" in {
            Delete(s"/topics/$topicId/answers").withEntity(
              deleteEntity(deleteRequest(answerId, secret))) ~> route ~> check {
              status shouldBe OK
            }
          }
        }
      }
    }
  }
}
