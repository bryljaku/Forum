package forum.repositories

import forum.models._
import java.time.ZonedDateTime

import forum.schemas.TopicsTable
import slick.dbio.DBIO
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api._
import pl.iterators.kebs._
import DateTimestampConversion._

class TopicsRepository {
  val topicsTable = TableQuery[TopicsTable]

  private def topicCheckSecret(id: Id, secret: Secret) =
    topicsTable.filter(t => t.id === id && t.secret === secret)

  def findTopic(topicId: Id): DBIO[Option[Topic]] =
    topicsTable
    .filter(_.id === topicId)
    .result
    .headOption

  def findTopics(offset: Int, limit: Int): DBIO[List[Topic]] =
    topicsTable.to[List]
    .sortBy(_.lastActivity.desc)
    .drop(offset)
    .take(limit)
    .result

  def addTopic(topic: Topic): DBIO[(Id, Secret)] =
    topicsTable returning topicsTable.map(x => (x.id, x.secret)) += topic

  def updateTopic(request: UpdateRequest): DBIO[Int] =
    topicCheckSecret(request.id, request.secret)
    .map(t => (t.content, t.lastActivity))
    .update((request.content, ZonedDateTime.now()))

  def deleteTopic(request: DeleteRequest): DBIO[Int] =
    topicCheckSecret(request.id, request.secret).delete
}
