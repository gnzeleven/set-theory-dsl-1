package org.cs474.setdsl

import SetDSL.*
import SetDSL.Expression.*
import utils.CreateLogger
import utils.TestExpressions.*

/**
 * Factory for [[SetPlayground]] instances
 * The workflow starts here
 * */
object SetPlayground {
  val logger = CreateLogger(this.getClass)

  /** Main Method
   *
   * @param args : Array[String] - command line input
   * @return Unit
   */
  def main(args: Array[String]): Unit = {
    logger.info("Inside main method... Evaluating some example expressions")

    /*
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
    logger.info("*************************************************************************************")

    // dummy set operations
    logger.info("Evaluating some dummy set expressions...")

    Assign(testVar1, testValue1).evaluate()
    Scope("scope1", Assign(testVar2, Insert(Seq(testValue2, Value("We gotta blend in. River dance!"))))).evaluate()
    testExpression1.evaluate()
    testExpression2.evaluate()

    logger.info("Union: " + Scope("scope1", Scope("scope2", Union(Var("set1"), Var("set2")))).evaluate())
    logger.info("Intersection: " + Scope("scope1", Scope("scope2", Intersection(Var("set1"), Var("set2")))).evaluate())
    logger.info("Difference: " + Scope("scope1", Scope("scope2", Difference(Var("set1"), Var("set2")))).evaluate())
    logger.info("SymmetricDifference: " +
      Scope("scope1", Scope("scope2", SymmetricDifference(Var("set1"), Var("set2")))).evaluate())
    logger.info("CartesianProduct: " +
      Scope("scope1", Scope("scope2", CartesianProduct(Var("set1"), Var("set2")))).evaluate())
    */
    // create a new variable - meaningOfLife and assign a value
    Assign(Var("meaningOfLife"), Value("The meaning of life is 42")).evaluate()
    // create a new variable - newMeaningOfLife and assign evaluation of an insert statement
    Assign(Var("newMeaningOfLife"), Insert(Seq(Var("meaningOfLife"), Value(42)))).evaluate()
    // trying to update a variable that doesnt exist will throw an exception that is handled
    Assign(Var("thisDoesntExist"), Update(Seq(Value("I do not exist")))).evaluate()
    Thread.sleep(100)
    // create a new macro and execute
    Macro("m", Var("meaningOfLife"))
    Assign(Var("varByMacro"), Insert(Seq(Value(true), Var("meaningOfLife")))).evaluate()
    // using scopes
    println(Scope("scope1", Assign(Var("varByScope"), Insert(Seq(Value(1.6180), Var("meaningOfLife"))))).evaluate())
    Scope("scope1", Scope("scope2", Check(Var("varByMacro"), Value(true)))).evaluate()
    // using set operations
    Scope("scope1", Union(Var("varByMacro"), Var("varByScope"))).evaluate()
    Scope("scope1", Intersection(Var("varByMacro"), Var("varByScope"))).evaluate()
    Scope("scope1", CartesianProduct(Var("varByMacro"), Var("varByScope"))).evaluate()
  }
}
