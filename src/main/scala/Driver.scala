package org.cs474.setdsl

import BasicArithmeticDSL.Expression.*
import Utils.{CreateLogger, ObtainConfigReference}

/**
 * Factory for [[Driver]] instances
 * The workflow starts here
 * */
object Driver {
  val logger = CreateLogger(this.getClass)
  /** Main Method
   * @param args : Array[String] - command line input
   * @return Unit
   */
  def main(args: Array[String]): Unit = {
    val config = ObtainConfigReference("DSL") match {
      case Some(value) => value
      case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
    }
    logger.info("works")
    val someExpression = Sub(Add(Add(Value(2), Value(3)),Var("z")), Var("x")).evaluate()
    println(someExpression)
  }
}
