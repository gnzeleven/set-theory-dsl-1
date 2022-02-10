package org.cs474.setdsl
import SetDSL.*
import SetDSL.Expression.*
import utils.ObtainConfigReference

import com.sun.org.apache.xpath.internal.operations.Variable
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.immutable.HashSet

class SetDSLTestSuite extends AnyFlatSpec with Matchers {

  val testValue1: Value = Value("The meaning of life is 42")
  val testVar1: Var = Var("meaningOfLife")
  Assign(testVar1, testValue1).evaluate()

  val testValue2: Value = Value("Kowalski, Analysis")
  val testVar2: Var = Var("skipper")
  Scope("scope1", Assign(testVar2, Insert(Seq(testValue2, Value("We gotta blend in. River dance!"))))).evaluate()

  val testExpression1: Expression = Scope(
    "scope1", Scope(
      "scope2", Assign(
        Var("set1"), Insert(
          Seq(Value("1.1618"), Value("e^(i*pi)=-1"), Value(1),
            Var("meaningOfLife"), Value(2), Value(3), Value(4),
            Value("This is from set1")
          )
        )
      )
    )
  )
  testExpression1.evaluate()

  val testExpression2: Expression = Scope(
    "scope1", Scope(
      "scope2", Assign(
        Var("set2"), Insert(
          Seq(Value("2.7182"), Value("May the force be with you!"), Value(1),
            Var("meaningOfLife"), Value("This is from set 2"), Value(4)
          )
        )
      )
    )
  )
  testExpression2.evaluate()

  // assert type expression
  it should "return an object of type Expression" in {
    assert(testValue1.isInstanceOf[Expression])
  }

  // check variable assignment in global scope
  it should "create a variable in global scope and assign a value" in {
    assert(testVar1.evaluate() == HashSet("The meaning of life is 42"))
  }

  // assert current scope
  it should "return the set stored in the Variable" +
    " set1 withing scope2 which is inside scope1" in {
    val testExpression = Scope("scope1", Scope("scope2", Var("set1")))
    assert(testExpression.evaluate().isInstanceOf[Set[Any]])
  }

  // assert macro creation and evaluation
  it should "create in a scope and evaluate macros in another level of scope" in {
    Macro("m", Var("meaningOfLife"))
    val testExpression = Scope(
      "scope1", Scope(
        "scope2", Assign(
          Var("newMeaningOfLife"), Insert(Seq(Macro("m"), Var("skipper")))
        )
      )
    )
    testExpression.evaluate()
    assert(Scope("scope1", Scope("scope2", Var("newMeaningOfLife"))).evaluate() ==
      Scope("scope1", testVar2).evaluate() ++ testVar1.evaluate())
  }

  // assert check expression
  it should "return true as testValue2 exists in set testVar2" in {
    val testExpression = Scope(
      "scope1", Scope(
        "scope2", Check(
          testVar2, testValue2
        )
      )
    )
    println(testExpression.evaluate() == HashSet(true))
  }

  // assert delete expression
  it should "delete testValue2 from the set testVar2" in {
    val testExpression = Scope(
      "scope1", Scope(
        "scope2", Assign(
          testVar2, Delete(Seq(testValue2))
        )
      )
    )
    println(testExpression.evaluate() == Scope("scope1", testVar2).evaluate() -- testValue2.evaluate())
  }

  // assert update expression
  it should "update testVar1 with new value" in {
    val testValue = Value("42 is the meaning of life")
    val testExpression = Scope(
      "scope1", Scope(
        "scope2", Assign(
          testVar1, Update(Seq(testValue))
        )
      )
    )
    println(testExpression.evaluate() == testValue1.evaluate() ++ testValue.evaluate())
  }

  // assert set operations
  val set1 = Scope("scope1", Scope("scope2", Var("set1"))).evaluate()
  val set2 = Scope("scope1", Scope("scope2", Var("set2"))).evaluate()

  // union operation
  it should "verify Union operation of two sets" in {
    val set = Scope("scope1", Scope("scope2", Union(Var("set1"), Var("set2")))).evaluate()
    assert(set == set1.++(set2))
  }

  // intersection operation
  it should "verify Intersection operation of two sets" in {
    val set = Scope("scope1", Scope("scope2", Intersection(Var("set1"), Var("set2")))).evaluate()
    assert(set == set1.intersect(set2))
  }

  // difference operation
  it should "verify Difference operation of two sets" in {
    val set = Scope("scope1", Scope("scope2", Difference(Var("set1"), Var("set2")))).evaluate()
    assert(set == set1.diff(set2))
  }

  // symmetric difference operation
  it should "verify SymmetricDifference operation of two sets" in {
    val set = Scope("scope1", Scope("scope2", SymmetricDifference(Var("set1"), Var("set2")))).evaluate()
    assert(set == set1.diff(set2).++(set2.diff(set1)))
  }

  // cartesian product operation
  it should "verify CartesianProduct operation of two sets" in {
    val set = Scope("scope1", Scope("scope2", CartesianProduct(Var("set1"), Var("set2")))).evaluate()
    // for comprehension in scala is implemented internally as a combination of flatmap and map
    val anotherSet = for {
      element1 <- set1
      element2 <- set2
    } yield((element1, element2))
    assert(set == anotherSet)
  }

}
