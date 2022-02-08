package org.cs474.setdsl
import scala.collection.mutable.*

object SetsDSL {
  type BasicType = Int
  enum Expression {
    case Variable(name: String)
    case Value(arg: Any)
    case Assign(variable: Variable, expression: Insert)
    case Insert(args: Expression*)
    case Macro(name: String, operation: Expression)
    case Scope(name: String, expression: Expression)
    case Var(name: String)
    case Add(exp1: Expression, exp2: Expression)
    case Sub(exp1: Expression, exp2: Expression)
    private val globalScope: Map[String, Any] = Map()

    def evaluate(): Any = {
      this match {
        case Value(i) => i
        case Variable(name: String) => {
          globalScope.get(name) match {
            case Some(v) => v
            case None => {
              None
            }
          }
        }

        case Assign(variable: Variable, expression: Insert) => {
          variable.evaluate() match {
            case v => v
            case None => {
              globalScope.put(variable.name, expression.evaluate())
              Variable(variable.name)
            }
          }
        }

        case Insert(args: ) => {

        }


        case Var(name: String) => globalScope(name)
        case Add(exp1: Expression, exp2: Expression) => exp1.evaluate() + exp2.evaluate()
        case Sub(exp1: Expression, exp2: Expression) => exp1.evaluate() - exp2.evaluate()
      }
    }
  }
}
