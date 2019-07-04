import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import com.typesafe.config.ConfigFactory
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent._
import org.scalatest.concurrent.ScalaFutures
import akka.http.scaladsl.marshalling.Marshal
import forum.models.{AnswerInput, Content, CreatedResponse, DeleteRequest, Id, Mail, Nickname, Protocols, Secret, TopicInput, TopicName, UpdateRequest}
import slick.jdbc.H2Profile.api.Database
import forum.Server.materializer
import ExecutionContext.Implicits.global
import forum.repositories.{AnswersRepository, TopicsRepository}
import forum.routes.Routes
import forum.services.{AnswersService, TopicsService}

import scala.concurrent.duration._

object RouteSpecHelper extends ScalaFutures with Protocols {
    val config = ConfigFactory.load()
    val url = s"http://${config.getString("app.interface")}:${config.getInt("app.port")}"

    val db: Database = Database.forConfig("postgres")
    val answersRepository = new AnswersRepository
    val topicsRepository = new TopicsRepository
    val topicsService = new TopicsService(db, topicsRepository)
    val answersService = new AnswersService(db, answersRepository)
    val routes = new Routes(db, topicsService, answersService)
    val route = routes.route

    val topicValid = TopicInput(Nickname("nickname"), Mail("mail@St.ring"), TopicName("topic: String"), Content("content: String"))
    val topicInvalid = TopicInput(Nickname("nickname"), Mail("mailSt.ring"), TopicName("topic: String"), Content("content: String"))
    def answerValid(topicId: Id) = AnswerInput(Nickname("nickname"), Mail("mail@Str.ing"), Content("content: String"))
    def answerInvalid(topicId: Id) = AnswerInput(Nickname("nickname"), Mail("mail@String."), Content("content: String"))
    def updateRequestValid(id: Id, secret: Secret) = UpdateRequest(id, secret, Content("randomContent"))
    def updateRequestInvalid(id: Id, secret: Secret) = UpdateRequest(id, secret, Content(""))
    def deleteRequest(id: Id, secret: Secret) = DeleteRequest(id, secret)

    def topicEntity(topicInput: TopicInput) = Marshal(topicInput).to[MessageEntity].futureValue
    def answerEntity(answerInput: AnswerInput) = Marshal(answerInput).to[MessageEntity].futureValue  
    def updateEntity(updateRequest: UpdateRequest) = Marshal(updateRequest).to[MessageEntity].futureValue
    def deleteEntity(deleteRequest: DeleteRequest) = Marshal(deleteRequest).to[MessageEntity].futureValue  
    
    def toResponseMessage(response: HttpResponse) = {
        val messageFuture = Unmarshal(response.entity).to[CreatedResponse]
        Await.result(messageFuture, 2.second)
    }
}