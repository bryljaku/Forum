package forum

import com.typesafe.config.ConfigFactory

import scala.language.postfixOps

object ContentAndPaginationValidation {

  val config = ConfigFactory.load()
  val topicsLimit = config.getInt("page.topicsLimit")
  val mailLimit = config.getInt("page.mailLimit")
  val answersLimit = config.getInt("page.answersLimit")
  val contentLimit = config.getInt("page.contentLimit")
  val topicLimit = config.getInt("page.topicLimit")
  private val emailRegex = """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$""".r

  def validateAndCorrectTopicsPagination(page: Option[Int], limit: Option[Int]) = {
    val limitVal: Int = limit match {
      case Some(l) if l <= topicsLimit => l
      case _ => topicsLimit
    }
    val pageVal: Int = page match {
      case Some(p) if p >= 0 => p
      case _ => 0
    }
    (pageVal, limitVal)
  }

  def validateAndCorrectAnswersPagination(before: Int, after: Int, mid: Int) = {
    def validatePagination = before + after + 1 <= answersLimit

    val correctedBefore = if (before > mid) mid else before

    def correctPagination = {
      val afterRatio = after / (correctedBefore + after).toFloat
      val beforeRatio = correctedBefore / (correctedBefore + after)
      ((beforeRatio * answersLimit) toInt, (afterRatio * answersLimit) toInt)
    }

    if (validatePagination)
      (correctedBefore, after)
    else
      correctPagination
  }

  def validateAnswerInput(answer: AnswerInput) = {
    validateMail(answer.mail) && validateContent(answer.content) && validateNickname(answer.nickname)
  }

  private def validateNickname(nickname: String) = nickname.size > 0

  private def validateMail(mail: String) = emailRegex.findFirstIn(mail) != None

  private def validateContent(content: String) = content.length > 0 && content.length < contentLimit

  def validateTopicInput(topic: TopicInput) = {
    validateMail(topic.mail) && validateContent(topic.content) && validateTopic(topic.topic) && validateNickname(topic.nickname)
  }

  private def validateTopic(topic: String) = topic.length > 0 && topic.length < topicLimit

  def validateUpdateRequest(request: UpdateRequest) = {
    validateContent(request.content)
  }
}