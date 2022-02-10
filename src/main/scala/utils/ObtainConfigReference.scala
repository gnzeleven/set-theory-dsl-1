package org.cs474.setdsl
package utils

import com.typesafe.config.{Config, ConfigFactory}
import scala.util.{Failure, Success, Try}

object ObtainConfigReference {

  private val config = ConfigFactory.load()
  private val logger = CreateLogger(classOf[this.type])

  private def ValidateConfig(confEntry: String): Boolean = {
    Try(config.getConfig(confEntry)) match {
      case Failure(ex) => {
        logger.error(s"Failed to retrieve config entry ${confEntry} for reason ${ex}")
        false
      }
      case Success(_) => true
    }
  }

  def apply(confEntry:String): Option[Config] = {
    if ValidateConfig(confEntry) then Some(config) else None
  }
}
