package org.cs474.setdsl
package Utils

import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Failure, Success, Try}

object CreateLogger {

  def apply[T](logClass: Class[T]): Logger = {
    val xmlpath = "logback.xml"
    val logger = LoggerFactory.getLogger(logClass)
    Try(getClass.getClassLoader.getResourceAsStream(xmlpath)) match {
      case Failure(ex) => logger.error(s"Failed to locate ${xmlpath} for reason ${ex}")
      case Success(inStream) => inStream.close()
    }
    logger
  }
}
