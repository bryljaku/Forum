import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import com.typesafe.config.ConfigFactory
import forum.Server.materializer
import akka.http.scaladsl.unmarshalling.Unmarshal
import scala.concurrent._
import scala.util.{ Failure, Success }
import org.scalatest.concurrent.ScalaFutures
import akka.http.scaladsl.marshalling.Marshal
import ExecutionContext.Implicits.global
import forum._
import akka.http.scaladsl.model.HttpMethods._
import scala.concurrent.duration._

object RouteSpecHelper extends ScalaFutures with Protocols {
    val config = ConfigFactory.load()
    val url = s"http://${config.getString("app.interface")}:${config.getInt("app.port")}"
  
    val topicValid = TopicInput("nickname", "mail@St.ring", "topic: String", "content: String")
    val topicInvalid = TopicInput("nickname", "mailSt.ring", "topic: String", "content: String")
    def answerValid(topicId: Int) = AnswerInput("nickname", "mail@Str.ing", "content: String")
    def answerInvalid(topicId: Int) = AnswerInput("nickname", "mail@String.", "content: String")
    def updateRequestValid(id: Int, secret: Int) = UpdateRequest(id, secret, "randomContent")
    def updateRequestInvalid(id: Int, secret: Int) = UpdateRequest(id, secret, "")
    def deleteRequest(id: Int, secret: Int) = DeleteRequest(id, secret)

    def topicEntity(topicInput: TopicInput) = Marshal(topicInput).to[MessageEntity].futureValue
    def answerEntity(answerInput: AnswerInput) = Marshal(answerInput).to[MessageEntity].futureValue  
    def updateEntity(updateRequest: UpdateRequest) = Marshal(updateRequest).to[MessageEntity].futureValue
    def deleteEntity(deleteRequest: DeleteRequest) = Marshal(deleteRequest).to[MessageEntity].futureValue  
    
    def toResponseMessage(response: HttpResponse) = {
        val messageFuture = Unmarshal(response.entity).to[ContentCreatedMessage]
        Await.result(messageFuture, 2.second)
    }
}