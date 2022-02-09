package org.cs474.setdsl
import org.cs474.setdsl.BasicSetDSL.Expression

import scala.collection.mutable

object BasicSetDSL {
  type BasicType = Set[Any]
  private val globalScope: mutable.Map[String, Any] = mutable.Map()

//  case class NewMacro(name: String, expression: Expression) {
//    globalScope.put(name, expression)
//  }

  object Macro {
    def apply(name: String): Expression = {
      globalScope.get(name) match {
        case Some(expression: Expression) => expression
        case None => Expression.Empty
      }
    }
    def apply(name: String, expression: Expression): Unit = {
      globalScope.put(name, expression)
    }
  }

  enum Expression {
    case Value(input: Any)
    case Var(name: String)
    case Insert(expressions: Seq[Expression])
    case Assign(variable: Var, set: Expression)
    case Delete(expressions: Seq[Expression])
    case Empty
    private case ExpressionsToSet(expressions: Seq[Expression])

    case Union(expression1: Expression, expression2: Expression)
    case Intersection(expression1: Expression, expression2: Expression)
    case Difference(expression1: Expression, expression2: Expression)
    case SymmetricDifference(expression1: Expression, expression2: Expression)
    case CartesianProduct(expression1: Expression, expression2: Expression)

    def evaluate(): Set[Any] = {
      this match {
        // value - returns set(value)
        case Value(i) => Set(i)

        // var - if var exists in the map returns a set
        // otherwise create a new set for the var
        case Var(name: String) => {
          globalScope.contains(name) match {
            case true => globalScope(name).asInstanceOf[Set[Any]]
            case false => {
              globalScope.put(name, Set())
              globalScope(name).asInstanceOf[Set[Any]]
            }
          }
        }

        // assign and insert - insert the elements
        // to original set if it exists or create
        // a new set with the inserted elements
        case Assign(variable: Var, set: Insert) => {
          val ret = variable.evaluate() ++ set.evaluate()
          globalScope.put(variable.name, ret)
          println(globalScope)
          ret
        }

        // assign and delete - delete the elements from
        // the set if the exist or return an empty set
        case Assign(variable: Var, set: Delete) => {
          val origSet = variable.evaluate()
          val ret = variable.evaluate() -- set.evaluate()
          println(globalScope)
          globalScope.put(variable.name, ret)
          println(globalScope)
          ret
        }

        case Assign(variable: Var, expression: Expression) => {
          val origSet = variable.evaluate()
          val ret = expression.evaluate()
          println(globalScope)
          globalScope.put(variable.name, ret)
          println(globalScope)
          ret
        }

        // empty - return an empty set
        case Empty => Set()

        // convert a sequence of expression wrapped within insert or
        // delete statement to a set of values and return the set
        case ExpressionsToSet(expressions: Seq[Expression]) => {
          def recursive(head: Expression, tail: Seq[Expression]): Set[Any] = {
            if (tail.length == 0) head.evaluate()
            else if (tail.length == 1) head.evaluate() ++ tail(0).evaluate()
            else head.evaluate() ++ recursive(tail(0), tail.slice(1, tail.length))
          }
          if (expressions.length == 1) {
            recursive(expressions(0), Seq())
          }
          recursive(expressions(0), expressions.slice(1, expressions.length))
        }

        // convert the sequence of expressions to a set
        // and return the set
        case Insert(expressions: Seq[Expression]) => {
          ExpressionsToSet(expressions).evaluate()
        }

        // convert the expression within delete statement to
        // a set of values and return the set
        case Delete(expressions: Seq[Expression]) => {
          ExpressionsToSet(expressions).evaluate()
        }

        case Union(expression1: Expression, expression2: Expression) => {
          expression1.evaluate() ++ expression2.evaluate()
        }

        case Intersection(expression1: Expression, expression2: Expression) => {
          expression1.evaluate() intersect expression2.evaluate()
        }

        case Difference(expression1: Expression, expression2: Expression) => {
          expression1.evaluate() diff expression2.evaluate()
        }

        case SymmetricDifference(expression1: Expression, expression2: Expression) => {
          val set1 = expression1.evaluate()
          val set2 = expression2.evaluate()
          val union = set1 ++ set2
          val intersect = set1 intersect set2
          union - intersect
        }

        case CartesianProduct(expression1: Expression, expression2: Expression) => {
          val set1 = expression1.evaluate()
          val set2 = expression2.evaluate()
          set1.flatMap(element1 => set2.map(element2 => (element1, element2)))
        }
      }
    }
  }
}
