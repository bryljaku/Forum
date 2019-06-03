package forum

import slick.jdbc.H2Profile.api.Database
import slick.jdbc.PostgresProfile.api._
import DateTimestampConversion._
import scala.concurrent.Future
import java.util.Date

class DbOperations extends DbBase with InputHandler {
  private def topicValidation(id: Int, secret: Int) = topicsTable.filter(t => t.id === id && t.secret === secret)
  private def answerValidation(id: Int, secret: Int) = answersTable.filter(a => a.id === id && a.secret === secret)
  private def updateTopicActivity(topicId: Int) = topicsTable.filter(_.id === topicId).map(_.lastActivity).update(new Date) // 

  def findTopics(page: Int, limit: Int): Future[List[Topic]] = topicsTable.to[List].sortBy(_.lastActivity.desc).drop(page * limit).take(limit).result
  def findTopic(topicId: Int): Future[Option[Topic]] = topicsTable.filter(_.id === topicId).result.headOption
  def findAnswer(answerId: Int): Future[Option[Answer]] = answersTable.filter(_.id === answerId).result.headOption

  def findTopicAnswers(topicId: Int, answerId: Int): Future[List[Answer]] = answersTable.filter(_.topicID === topicId).to[List].sortBy(_.lastActivity.desc).result

  def createTopic(topic: TopicInput): Future[Int] = topicsTable returning topicsTable.map(_.secret) += topicFromInput(topic)
  def createAnswer(answer: AnswerInput): Future[Int] = answersTable returning answersTable.map(_.secret) += answerFromInput(answer) // andThen updateTopicActivity

  def updateTopic(request: UpdateRequest): Future[Int] =
    topicValidation(request.id, request.secret)
    .map(t => (t.content, t.lastActivity))
    .update((request.content, new Date))

  def updateAnswer(request: UpdateRequest): Future[Int] =
    answerValidation(request.id, request.secret)
    .map(a => (a.content, a.lastActivity))
    .update((request.content, new Date))

  def deleteTopic(request: DeleteRequest): Future[Int] = topicValidation(request.id, request.secret).delete // answers cascade on delete 
  def deleteAnswer(request: DeleteRequest): Future[Int] = answerValidation(request.id, request.secret).delete
}
