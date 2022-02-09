package org.cs474.setdsl
import scala.collection.mutable

object SetDSL {
  private val globalScope: mutable.Map[String, Any] = mutable.Map()
  private val stringToScope: mutable.Map[String, mutable.Map[String, Any]] = mutable.Map()
  private var currentScope = globalScope
  private val executionScope: mutable.Map[String, Any] = mutable.Map()

  object Macro {
    def apply(name: String): Expression = {
      executionScope.get(name) match {
        case Some(expression: Expression) => expression
        case None => Expression.Empty
      }
    }
    def apply(name: String, expression: Expression): Unit = {
      currentScope.put(name, expression)
      executionScope ++= currentScope
    }
  }

  enum Expression {
    case Value(input: Any)
    case Var(name: String)
    case Insert(expressions: Seq[Expression])
    case Assign(expression1: Expression, expression2: Expression)
    case Delete(expressions: Seq[Expression])
    case Scope(name: String, expression: Expression)
    case Check(variable: Var, value: Value)
    case Empty
    private case ExpressionsToSet(expressions: Seq[Expression])

    case Union(expression1: Expression, expression2: Expression)
    case Intersection(expression1: Expression, expression2: Expression)
    case Difference(expression1: Expression, expression2: Expression)
    case SymmetricDifference(expression1: Expression, expression2: Expression)
    case CartesianProduct(expression1: Expression, expression2: Expression)

    def evaluate(): Set[Any] ={
      executionScope.clear()
      executionScope ++= globalScope
      currentScope = globalScope
      this._evaluate()
    }

    def _evaluate(searchScope: mutable.Map[String, Any] = executionScope): Set[Any] = {
      this match {
        // value - returns set(value)
        case Value(i) => Set(i)

        // var - if var exists in the map returns a set
        // otherwise create a new set for the var
        case Var(name: String) => {
          searchScope.contains(name) match {
            case true => searchScope(name).asInstanceOf[Set[Any]]
            case false => {
              searchScope.put(name, Set())
              executionScope ++= searchScope
              searchScope(name).asInstanceOf[Set[Any]]
            }
          }
        }

        // assign and insert - insert the elements
        // to original set if it exists or create
        // a new set with the inserted elements
        case Assign(variable: Var, set: Insert) => {
          val ret = variable._evaluate(currentScope) ++ set._evaluate()
          currentScope.put(variable.name, ret)
          executionScope ++= currentScope
          println(searchScope)
          ret
        }

        // assign and delete - delete the elements from
        // the set if the exist or return an empty set
        case Assign(variable: Var, set: Delete) => {
          val ret = variable._evaluate() -- set._evaluate()
          println(searchScope)
          searchScope.put(variable.name, ret)
          println(searchScope)
          ret
        }

        // This is for macro - dont erase
        case Assign(variable: Var, expression: Expression) => {
          val ret = expression._evaluate()
          searchScope.put(variable.name, ret)
          println(searchScope)
          ret
        }

        // empty - return an empty set
        case Empty => Set()

        // convert a sequence of expression wrapped within insert or
        // delete statement to a set of values and return the set
        case ExpressionsToSet(expressions: Seq[Expression]) => {
          def recursive(head: Expression, tail: Seq[Expression]): Set[Any] = {
            if (tail.length == 0) head._evaluate()
            else if (tail.length == 1) head._evaluate() ++ tail(0)._evaluate()
            else head._evaluate() ++ recursive(tail(0), tail.slice(1, tail.length))
          }
          if (expressions.length == 1) {
            recursive(expressions(0), Seq())
          }
          recursive(expressions(0), expressions.slice(1, expressions.length))
        }

        // convert the sequence of expressions to a set
        // and return the set
        case Insert(expressions: Seq[Expression]) => {
          ExpressionsToSet(expressions)._evaluate()
        }

        // convert the expression within delete statement to
        // a set of values and return the set
        case Delete(expressions: Seq[Expression]) => {
          ExpressionsToSet(expressions)._evaluate()
        }

        case Check(variable: Var, value: Value) => {
          Set(variable._evaluate() contains value._evaluate()(0))
        }

        case Union(expression1: Expression, expression2: Expression) => {
          expression1._evaluate() ++ expression2._evaluate()
        }

        case Intersection(expression1: Expression, expression2: Expression) => {
          expression1._evaluate() intersect expression2._evaluate()
        }

        case Difference(expression1: Expression, expression2: Expression) => {
          expression1._evaluate() diff expression2._evaluate()
        }

        case SymmetricDifference(expression1: Expression, expression2: Expression) => {
          val set1 = expression1._evaluate()
          val set2 = expression2._evaluate()
          val union = set1 ++ set2
          val intersect = set1 intersect set2
          union - intersect
        }

        case CartesianProduct(expression1: Expression, expression2: Expression) => {
          val set1 = expression1._evaluate()
          val set2 = expression2._evaluate()
          set1.flatMap(element1 => set2.map(element2 => (element1, element2)))
        }

        case Scope(name: String, expression: Expression) => {
          if (!currentScope.contains(name)) {
            val newMap = mutable.Map[String, Any]()
            currentScope.put(name, newMap)
          }
          executionScope ++= currentScope
          currentScope = currentScope(name).asInstanceOf[mutable.Map[String, Any]]
          expression._evaluate()
        }
      }
    }
  }
}

