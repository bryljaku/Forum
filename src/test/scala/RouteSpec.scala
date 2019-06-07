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


class RouteSpec extends WordSpec with Matchers with ScalatestRouteTest {
"Service" should {
    "return status ok when getting topics list" in {
        topicsGetRequest ~> route ~> check {
            status shouldBe OK
        }
    }
    "respond with 404 error when getting topic which does not exist" in {
        Get("/topics/123457") ~> route ~> check {
            status shouldBe NotFound
      }
    }  
    "respond status ok when adding topic" in {
        topicPostRequest ~> route ~> check {
            status shouldBe Created
        }
    }
    "respond status BadRequest when adding invalid topic" in {
        topicInvalidPostRequest ~> route ~> check {
            status shouldBe BadRequest
        }
    }
    
}
}