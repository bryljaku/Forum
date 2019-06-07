package forum

import slick.jdbc.H2Profile.api.Database
import slick.jdbc.PostgresProfile.api._
import DateTimestampConversion._
import scala.concurrent.Future
import java.util.Date
import scala.language.postfixOps
import com.typesafe.config.ConfigFactory
import ContentAndPaginationValidation._

object AnswersService extends DbBase with InputHandler {
  private def answerCheckSecret(id: Int, secret: Int) = answersTable.filter(a => a.id === id && a.secret === secret)

  def findTopicAnswers(topicId: Int, mid: Int, before: Int, after: Int) = { 
    def findAction(topicId: Int, mid: Int, before: Int, after: Int): Future[List[Answer]] = 
      answersTable.filter(_.topicID === topicId).to[List]
      .sortBy(_.lastActivity.asc)
      .drop(mid - before)
      .take(before + 1 + after).result

      val (beforeVal, afterVal): (Int, Int) = validateAndCorrectAnswersPagination(before, after, mid)
      findAction(topicId, mid, beforeVal, afterVal)
  }
  def findAnswer(answerId: Int): Future[Option[Answer]] = answersTable.filter(_.id === answerId).result.headOption
  
  def createAnswer(answer: AnswerInput, topicId: Int): Option[Future[(Int, Int)]] = {
    def insertAction: Future[(Int, Int)] = answersTable returning answersTable.map(x => (x.id, x.secret)) += answerFromInput(answer, topicId)
    
    if (validateAnswerInput(answer)) Some(insertAction)
    else None
  }
  def updateAnswer(request: UpdateRequest): Option[Future[Int]] = {
    def updateAction: Future[Int] = answerCheckSecret(request.id, request.secret)
      .map(a => (a.content, a.lastActivity))
      .update((request.content, new Date))
      
      if (validateUpdateRequest(request)) Some(updateAction)
      else None
}
  def deleteAnswer(request: DeleteRequest): Future[Int] = answerCheckSecret(request.id, request.secret).delete
      
}
