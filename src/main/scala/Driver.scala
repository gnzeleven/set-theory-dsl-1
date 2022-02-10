package org.cs474.setdsl

import SetDSL.*
import SetDSL.Expression.*
import utils.CreateLogger
import utils.TestExpressions.*

/**
 * Factory for [[Driver]] instances
 * The workflow starts here
 * */
object Driver {
  val logger = CreateLogger(this.getClass)

  /** Main Method
   *
   * @param args : Array[String] - command line input
   * @return Unit
   */
  def main(args: Array[String]): Unit = {
    logger.info("Inside main method... Evaluating some example expressions")

    testValue.evaluate()
    testVar.evaluate()

    // trying to update a variable that doesnt exist will throw an exception that is handled
    Assign(Var("thisDoesntExist"), Update(Seq(Value("I am trying to update a variable that doesn't exist")))).evaluate()
    Thread.sleep(100)

    logger.info("Evaluation: " + Assign(testVar, testValue).evaluate())

    // trying to delete a variable that doesnt exist will throw an exception that is handled
    Assign(Var("thisDoesntExist"), Delete(Seq(Value("I am trying to delete a variable that doesn't exist")))).evaluate()
    Thread.sleep(100)

    Macro("m", Var("meaningOfLife"))

    val testExpression = Scope(
      "scope1", Scope(
        "scope2", Assign(
          Var("newMeaningOfLife"), Insert(Seq(Macro("m")))
        )
      )
    )

    logger.info("Evaluation: " + testExpression.evaluate())

    logger.info("Evaluation: " + Scope("scope1", Scope("scope2", Var("newMeaningOfLife"))).evaluate())
  }
}
