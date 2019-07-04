package forum.migration

import org.flywaydb.core.Flyway
import com.typesafe.config.ConfigFactory

class Migration {
  val config = ConfigFactory.load()

  private val flyway = new Flyway
  flyway.setDataSource(config.getString("postgres.url"), config.getString("postgres.user"), config.getString("postgres.password"))

  def migrate  = flyway.migrate()

  def reloadSchema = {
    flyway.clean()
    println("clean done")
    migrate
  }
}

