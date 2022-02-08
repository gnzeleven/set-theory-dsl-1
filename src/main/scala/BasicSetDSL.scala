package org.cs474.setdsl
import scala.collection.mutable

object BasicSetDSL {
  type BasicType = List[Any]
  enum Expression {
    case Value(input: Any)
    case Var(name: String)
    case Insert(args: List[Expression])
    case Assign(variable: Var, set: Insert)
    case Delete(from: Var, what: Expression)
    case Add(exp1: Expression, exp2: Expression)
    case Sub(exp1: Expression, exp2: Expression)
    private val globalScope: mutable.Map[String, List[Any]] = mutable.Map()

    def evaluate(): List[Any] = {
      this match {
        case Value(i) => List(i)
        case Var(name: String) => {
          globalScope.get(name) match {
            case Some(v) => v
            case _ => {
              globalScope ++ Map(name -> List())
              globalScope(name)
            }
          }
        }

        case Assign(variable: Var, set: Insert) => {
          val ret = variable.evaluate() ++ set.evaluate()
          globalScope.put(variable.name, ret)
          ret
        }

        case Insert(args: List[Expression]) => {
          def recursive(head: Expression, tail: List[Expression]): List[Any] = {
            if (tail.size == 0) head.evaluate()
            head.evaluate() ++ recursive(tail.head, tail.tail)
          }
          recursive(args.head, args.tail)
        }

        case Delete(from: Var, what: Expression) => {
          from.evaluate()
        }
        case Add(exp1: Expression, exp2: Expression) => exp1.evaluate() + exp2.evaluate()
        case Sub(exp1: Expression, exp2: Expression) => exp1.evaluate() - exp2.evaluate()
      }
    }
  }
}
