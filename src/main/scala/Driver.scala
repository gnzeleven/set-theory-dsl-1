package org.cs474.setdsl

import Utils.{CreateLogger, ObtainConfigReference}

/**
 * Factory for [[Driver]] instances
 * The workflow starts here
 * */
object Driver {
  val logger = CreateLogger(this.getClass)
  /** Main Method - Triggers the gRPC or REST server based on command line input
   * @param args : Array[String] - command line input
   * @return Unit
   */
  def main(args: Array[String]) = {
    val config = ObtainConfigReference("DSL") match {
      case Some(value) => value
      case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
    }
    logger.info("works")
  }
}
