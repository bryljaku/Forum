import akka.http.scaladsl.model._
import org.scalatest.{Matchers, WordSpec}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.testkit.ScalatestRouteTest
import routing.Routing.route
import RequestHelpers._
import models.ContentType._
import scala.collection._

//post http://localhost:8080/topics < src/main/resources/json/topicInput.json
//post http://localhost:8080/answers < src/main/resources/json/answerInput.json

val topicInputEntity 
class RoutingSpec extends WordSpec with Matchers with ScalatestRouteTest {
"Service" should {
    "return a list of topics" in {
        Get(topics) ~> route ~> check {
            status shouldBe OK
        }
    }
    "return secret when adding topic" in {
        Post("/topics").withEntity(topicInputEntity) ~> route ~> check {
            responseAs[SuccessMessage] shouldEqual SuccessMessage(_)
        }
    }

}
}