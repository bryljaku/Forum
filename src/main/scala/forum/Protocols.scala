package forum

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import java.sql.Timestamp
import scala.language.implicitConversions
import java.util.Date
// import org.joda.time.{LocalDateTime => JodaLocalDateTime}

trait ForumJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
    import DateToTimestamp._
    implicit val topicFormat = jsonFormat6(Topic)
    implicit val answerFormat = jsonFormat6(Answer)
}

object DateToTimestamp {
  implicit def dateToTimestamp(date: Date): Timestamp = new Timestamp(date.getTime)
}


// object jod {
// implicit val jodaLocalDateTimeFormat: JsonFormat[JodaLocalDateTime] = 
//   new JsonFormat[JodaLocalDateTime] {
//     override def write(obj: JodaLocalDateTime): JsValue = JsString(obj.toString)

//     override def read(json: JsValue): JodaLocalDateTime = json match {
//       case JsString(s) => Try(JodaLocalDateTime.parse(s)) match {
//         case Success(result) => result
//         case Failure(exception) => 
//           deserializationError(s"could not parse $s as Joda LocalDateTime", exception)
//       }
//       case notAJsString => 
//         deserializationError(s"expected a String but got a $notAJsString")
//     }
//   }
// }