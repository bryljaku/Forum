package forum.repositories

import java.time.ZonedDateTime
import forum.DateTimestampConversion._
import forum.{DeleteRequest, Id, Secret, Topic, TopicInput, TopicsTable, UpdateRequest}
import slick.dbio.DBIO
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api._
import pl.iterators.kebs._

class TopicsRepository extends Kebs {
  val topicsTable = TableQuery[TopicsTable]

  private def topicCheckSecret(id: Id, secret: Secret) =
    topicsTable.filter(t => t.id === id && t.secret === secret)

  def findTopicAction(topicId: Id): DBIO[Option[Topic]] =
    topicsTable
    .filter(_.id === topicId)
    .result
    .headOption

  def findTopicsAction(offset: Int, limit: Int): DBIO[List[Topic]] =
    topicsTable.to[List]
    .sortBy(_.lastActivity.desc)
    .drop(offset)
    .take(limit)
    .result

  def createTopicAction(topic: Topic): DBIO[(Id, Secret)] =
    topicsTable returning topicsTable.map(x => (x.id, x.secret)) += topic

  def topicUpdateAction(request: UpdateRequest): DBIO[Int] =
    topicCheckSecret(request.id, request.secret)
    .map(t => (t.content, t.lastActivity))
    .update((request.content, ZonedDateTime.now()))

  def deleteTopicAction(request: DeleteRequest): DBIO[Int] =
    topicCheckSecret(request.id, request.secret).delete

}
