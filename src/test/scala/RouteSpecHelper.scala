import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import scala.concurrent.Future
import forum.Server._
import akka.http.scaladsl.unmarshalling.Unmarshal


object RouteSpecHelper {
    val config = ConfigFactory.load()
    val url = s"http://${config.getString("app.interface")}:${config.getInt("app.port")}"
      val topicJson = ByteString(
    s"""
       |{"topicInput":
       |{
       |"nick":"testnick",
       |"email":"test@mail.com"},
       |"topic":"testtopic",
       |"content":"testcontent"
       |}
        """.stripMargin)
      val topicInvalidJson = ByteString(
    s"""
       |{"topicInput":
       |{
       |"nick":"testnick",
       |"email":"test@mailcom"},
       |"topic":"testtopic",
       |"content":"testcontent"
       |}
        """.stripMargin)
      val answerJson = ByteString(
    s"""
       |{"answerInput":
       |{
       |"nick":"testnick",
       |"email":"test@mail.com"},
       |"content":"testcontent"
       |}
        """.stripMargin)
      val answerInvalidJson = ByteString(
    s"""
       |{"answerInput":
       |{
       |"nick":"testnick",
       |"email":"test@mailcom"},
       |"content":"testcontent"
       |}
        """.stripMargin)
      def updateJson(id: Int, s: Int) = ByteString(
    s"""
       |{"updateRequest":
       |{
       |"id":$id,
       |"secret":$s},
       |"content":"testcontent"
       |}
        """.stripMargin)
      def updateInvalidJson(id: Int, secret: Int) = ByteString(
    s"""
       |{"updateRequest":
       |{
       |"id":$id,
       |"secret":$secret},
       |"content":""
       |}
        """.stripMargin)
      def deleteJson(id: Int, secret: Int) = ByteString(
    s"""
       |{"deleteRequest":
       |{
       |"id":$id,
       |"secret":$secret,
       |}
        """.stripMargin)

      def entityAnswer = HttpEntity(MediaTypes.`application/json`, answerJson)
      def entityAnswerInvalid = HttpEntity(MediaTypes.`application/json`, answerInvalidJson)
      def entityTopic = HttpEntity(MediaTypes.`application/json`, topicJson)
      def entityTopicInvalid = HttpEntity(MediaTypes.`application/json`, topicInvalidJson)
      def entityDelete(id: Int, secret: Int) = HttpEntity(MediaTypes.`application/json`, deleteJson(id, secret))
      def entityUpdate(id: Int, secret: Int) = HttpEntity(MediaTypes.`application/json`, updateJson(id, secret))
      def entityUpdateInvalid(id: Int, secret: Int) = HttpEntity(MediaTypes.`application/json`, updateInvalidJson(id, secret))


      def topicsGetRequest = HttpRequest(
        HttpMethods.GET,
        uri = url + "/topics"
      )
      def topicPostRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/topics",
        entity = entityTopic
      )
      def topicInvalidPostRequest = HttpRequest(
        HttpMethods.POST,
        uri = "/topics",
        entity = entityTopicInvalid
      ) 
      def answerPostRequest(topicId: Int) = HttpRequest(
        HttpMethods.POST,
        uri = s"/topics/$topicId",
        entity = entityAnswer
      )
      def answerInvalidPostRequest(topicId: Int) = HttpRequest(
        HttpMethods.POST,
        uri = s"/topics/$topicId",
        entity = entityAnswerInvalid
      )
      def deleteTopicRequest(id: Int, secret: Int) = HttpRequest(
        HttpMethods.DELETE,
        uri = s"topics/$id",
        entity = entityDelete(id, secret)
      )
      def updateTopicRequest(id: Int, secret: Int) = HttpRequest(
        HttpMethods.PUT,
        uri = s"topics/$id",
        entity = entityUpdate(id, secret)
      )
      def updateTopicInvalidRequest(id: Int, secret: Int) = HttpRequest(
        HttpMethods.PUT,
        uri = s"topics/$id",
        entity = entityUpdate(id, secret)
      )
      def updateAnswerRequest(topicId: Int, id: Int, secret: Int) = HttpRequest(
        HttpMethods.PUT,
        uri = s"topics/$id",
        entity = entityUpdate(id, secret)
      )
      def updateAnswerInvalidRequest(topicId: Int, id: Int, secret: Int) = HttpRequest(
        HttpMethods.PUT,
        uri = s"topics/$id",
        entity = entityUpdate(id, secret)
      )
      // def addContent(f: HttpRequest): Future[(Int, Int)] = {
      //   Unmarshall(Http().singleRequest(f))
      // }

}