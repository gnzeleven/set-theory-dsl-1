package org.cs474.setdsl

//import BasicArithmeticDSL.Expression

import SetDSL.*
import SetDSL.Expression.*
import Utils.{CreateLogger, ObtainConfigReference}

//import scala.collection.*

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
    val config = ObtainConfigReference("DSL") match {
      case Some(value) => value
      case None => throw new RuntimeException("Cannot obtain a reference to the config data.")
    }
    logger.info("works")
    //Assign(Var("xy"), Value("spl")).evaluate()
    //    Scope("scope1", Assign(Var("set_"), Insert(Seq(Var("xy"), Value(88))))).evaluate()
    //    Scope("scope1", Assign(Var("set1"), Insert(Seq(Var("set_"), Value(100), Value(true))))).evaluate()
    //    Scope("scope1", Assign(Var("set2"), Insert(Seq(Var("set_"), Value(105), Value(true))))).evaluate()
    //Scope("scope1", Assign(Var("set2"), Insert(Seq(Var("set_"), Value(12))))).evaluate()
    //Assign(Var("x"), Insert(Seq(Var("xy"), Value("xy"), Value(12)))).evaluate()
    //Scope("scope1", Scope("scope2", Assign(Var("x"), Insert(Seq(Var("xy"), Value("scop1")))))).evaluate()
    //println(Scope("scope1", Scope("scope2", Check(Var("x"), Value("scop1")))).evaluate())
    //Scope("scope1", Scope("scope2", Assign(Var("x"), Insert(Seq(Var("xy"), Value(42), Value("works?")))))).evaluate()
    //Macro("m", Delete(Seq(Var("xy"))))
    //println(Scope("scope1", Scope("scope2", Assign(Var("x"), Macro("m")))).evaluate())

    //    println(Scope("scope1", Union(Var("set1"), Var("set2"))).evaluate())
    //    println(Scope("scope1", Intersection(Var("set1"), Var("set2"))).evaluate())
    //    println(Scope("scope1", Difference(Var("set1"), Var("set2"))).evaluate())
    //    println(Scope("scope1", SymmetricDifference(Var("set1"), Var("set2"))).evaluate())
    val testValue: Value = Value("The meaning of life is 42")
    val testVar: Var = Var("meaningOfLife")

    Assign(Var("thisDoesntExist"), Update(Seq(Value("I am trying to update a variable that doesn't exist")))).evaluate()
    println(Assign(testVar, testValue).evaluate())

    Assign(Var("thisDoesntExist"), Delete(Seq(Value("I am trying to delete a variable that doesn't exist")))).evaluate()

    Macro("m", Var("meaningOfLife"))

    val testExpression = Scope(
      "scope1", Scope(
        "scope2", Assign(
          Var("newMeaningOfLife"), Insert(Seq(Macro("m")))
        )
      )
    )
    println(testExpression.evaluate())

    println(Scope("scope1", Scope("scope2", Var("newMeaningOfLife"))).evaluate())

//    val testExpression1: Expression = Scope(
//      "scope1", Scope(
//        "scope2", Assign(
//          Var("set1"), Insert(
//            Seq(Value("1.1618"), Value("e^(i*pi)=-1"), Value(1),
//              Var("meaningOfLife"), Value(2), Value(3), Value(4),
//              Value("This is from set1")
//            )
//          )
//        )
//      )
//    )
//    testExpression1.evaluate()



  }
}
