package forum

import slick.jdbc.H2Profile.api.Database
import slick.jdbc.PostgresProfile.api._
import DateTimestampConversion._
import scala.concurrent.Future
import java.util.Date
import scala.language.postfixOps
import com.typesafe.config.ConfigFactory

object Validation {
    val config = ConfigFactory.load()
    val topicsLimit = config.getInt("page.topicsLimit")
    val mailLimit = config.getInt("page.mailLimit")
    val answersLimit = config.getInt("page.answersLimit")
    val contentLimit = config.getInt("page.contentLimit")
    val topicLimit = config.getInt("page.topicLimit")
    
    private def validateNickname(nickname: String) = nickname.size > 0
    private def validateMail(mail: String) = if ("""(?=[^\s]+)(?=(\w+)@([\w\.]+))""".r.findFirstIn(mail) == None) false else true
    private def validateContent(content: String) = content.size > 0 && content.size < contentLimit
    private def validateTopic(topic: String) = topic.size > 0 && topic.size < topicLimit
    
    def validateTopicsPagination(page: Option[Int], limit: Option[Int]) = {
        val limitVal: Int = limit match {
            case Some(l) if l <= topicsLimit => l
            case _ => topicsLimit
        }
        val pageVal: Int = page match {
            case Some(l) if l <= topicsLimit => l
            case _ => topicsLimit
        }   
        (pageVal, limitVal)
    }
    def validateAnswersPagination(before: Int, after: Int) = {
        def validatePagination = before + after + 1 <= answersLimit
        def correctPagination = {
            val afterRatio = after / (before + after)
            val beforeRatio = before / (before + after)
            ((beforeRatio * answersLimit) toInt, (afterRatio * answersLimit) toInt)
        }

        if (validatePagination)
            (before, after)
        else 
            correctPagination
        }
    def validateAnswerInput(answer: AnswerInput) = {
        validateMail(answer.mail) && validateContent(answer.content) && validateNickname(answer.nickname)
    }
    def validateTopicInput(topic: TopicInput) = {
        validateMail(topic.mail) && validateContent(topic.content) && validateTopic(topic.topic) && validateNickname(topic.nickname)
    }
    def validateUpdateRequest(request: UpdateRequest) = {
        validateContent(request.content)
    }
}