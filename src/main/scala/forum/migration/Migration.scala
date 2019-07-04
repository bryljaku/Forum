package forum.migration

import org.flywaydb.core.Flyway
import com.typesafe.config.ConfigFactory

class Migration {
  val config = ConfigFactory.load()

  private val flyway = new Flyway
  flyway.setDataSource(config.getString("postgres.url"), config.getString("postgres.user"), config.getString("postgres.password"))

  def migrate  = {
    println("migrate")
    flyway.migrate()
  }

  def clean() ={
    println("clean")
    flyway.clean()
  }
  def reloadSchema = {
    clean
    migrate
  }
}

