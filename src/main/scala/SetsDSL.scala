package org.cs474.setdsl

object SetsDSL {
  type BasicType = Int
  enum Expression[Any] {
    case Variable(name: String)
    case Value(arg: Any)
    case Assign(variable: Variable[String], expression: Insert[List[Expression[Any]]])
    case Insert(args: List[Expression[Any]])
    case Macro(name: String, operation: Expression[Any])
    case Scope(name: String, expression: Expression[Any])
    case Var(name: String)
    case Add(exp1: Expression[Any], exp2: Expression[Any])
    case Sub(exp1: Expression[Any], exp2: Expression[Any])
    private val bindingScoping: Map[String, Any] = Map()

    def evaluate(): Any = {
      this match {
        case Value(i) => i
        case Var(name: String) => bindingScoping(name)
        case Add(exp1: Expression[Any], exp2: Expression[Any]) => exp1.evaluate() + exp2.evaluate()
        case Sub(exp1: Expression[Any], exp2: Expression[Any]) => exp1.evaluate() - exp2.evaluate()
      }
    }
  }
}
