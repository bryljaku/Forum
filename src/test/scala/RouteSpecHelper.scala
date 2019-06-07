import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.util.ByteString
import com.typesafe.config.ConfigFactory
import scala.concurrent.Future
import forum.Server._


object RouteSpecHelper {
      val topic = ByteString(
    s"""
       |{"topic":{"nick":"testnick","email":"test@mail.com"},
       |"topic":"testtopic","content":"testcontent"}
        """.stripMargin)

}