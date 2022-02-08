package org.cs474.setdsl
import scala.collection.mutable

object BasicSetDSL {
  type BasicType = Set[Any]
  enum Expression {
    case Value(input: Any)
    case Var(name: String)
    case Insert(args: Seq[Expression])
    case Assign(variable: Var, set: Insert)
    case Delete(expression: Expression)
    case Add(exp1: Expression, exp2: Expression)
    case Sub(exp1: Expression, exp2: Expression)
    private val globalScope: mutable.Map[String, Set[Any]] = mutable.Map()

    def evaluate(): Set[Any] = {
      this match {
        case Value(i) => Set(i)
        case Var(name: String) => {
          globalScope.get(name) match {
            case Some(v) => v
            case _ => {
              globalScope ++ Map(name -> Set())
              globalScope(name)
            }
          }
        }

        case Assign(variable: Var, set: Insert) => {
          val ret = variable.evaluate() ++ set.evaluate()
          globalScope.put(variable.name, ret)
          ret
        }

        case Insert(args: Seq[Expression]) => {
          def recursive(head: Expression, tail: Seq[Expression]): Set[Any] = {
            if (tail.length == 0) head.evaluate()
            head.evaluate() ++ recursive(tail(0), tail.slice(1, tail.length))
          }
          recursive(args(0), args.slice(1, args.length))
        }

        case Delete(expression: Expression) => {
          expression.evaluate()
        }
        case Add(exp1: Expression, exp2: Expression) => exp1.evaluate() + exp2.evaluate()
        case Sub(exp1: Expression, exp2: Expression) => exp1.evaluate() - exp2.evaluate()
      }
    }
  }
}
