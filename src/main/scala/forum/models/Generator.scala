package forum

import java.util.UUID
import scala.util.Random

object Generator {
  def getSecret = Secret(1000 + new Random().nextInt(9000))
  def getId = Id(UUID.randomUUID())
}

