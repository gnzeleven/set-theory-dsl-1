package org.cs474.setdsl

object BasicArithmeticDSL {
  type BasicType = Int
  enum Expression {
    case Value(input: BasicType)
    case Var(name: String)
    case Add(exp1: Expression, exp2: Expression)
    case Sub(exp1: Expression, exp2: Expression)
    private val bindingScoping: Map[String, Int] = Map("x" -> 2, "y" -> 8, "z" -> 5)

    def evaluate(): BasicType = {
      this match {
        case Value(i) => i
        case Var(name: String) => bindingScoping(name)
        case Add(exp1: Expression, exp2: Expression) => exp1.evaluate() + exp2.evaluate()
        case Sub(exp1: Expression, exp2: Expression) => exp1.evaluate() - exp2.evaluate()
      }
    }
  }
}
