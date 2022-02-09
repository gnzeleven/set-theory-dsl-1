package org.cs474.setdsl

//import BasicArithmeticDSL.Expression
import BasicSetDSL.*
import BasicSetDSL.Expression.*
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
    //val someExpression = Sub(Add(Add(Value(2), Value(3)),Var("z")), Var("x")).evaluate()
    Assign(Var("x"), Insert(Seq(Var("xy"), Value("xy"), Value(12)))).evaluate()
    Assign(Var("x"), Insert(Seq(Value(1)))).evaluate()
    val aMacro = Macro("aMacro", Var("x"))
    val finalExp = Assign(Var("z"), Macro("aMacro"))
    println(finalExp.evaluate())

//    val set1 = Set(1,2,3,4)
//    val set2 = Set(3,4,5,6)
//    val fm = set1.flatMap(element1 => set2.map(element2 => (element1, element2)))
//    println(fm)
//
//    val map = scala.collection.mutable.Map[String, Any]()
//    map.put("a", 2)
//    println(map.get("b"))
  }
}
