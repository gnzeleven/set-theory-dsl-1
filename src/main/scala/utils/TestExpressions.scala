package org.cs474.setdsl
package utils

import SetDSL.{Expression, Macro}
import SetDSL.Expression.*

/**
 * Factory for [[TestExpressions]] instances
 * This object contains some example test expressions that can be evaluated
 * */
object TestExpressions {

  val testValue: Value = Value("The meaning of life is 42")
  val testVar: Var = Var("meaningOfLife")

  val testValue1: Value = Value("The meaning of life is 42")
  val testVar1: Var = Var("meaningOfLife")

  val testValue2: Value = Value("Kowalski, Analysis")
  val testVar2: Var = Var("skipper")

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

  val testScopeExpression1: Expression = Scope("scope1", Scope("scope2", Var("set1")))
  val testScopeExpression2: Expression = Scope("scope1", Scope("scope2", Var("set2")))
}
