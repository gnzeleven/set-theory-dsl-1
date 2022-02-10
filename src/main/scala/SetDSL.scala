package org.cs474.setdsl
import org.cs474.setdsl.Utils.CreateLogger

import scala.collection.immutable.HashSet
import scala.collection.mutable

/** Factory for [[SetDSL]] instances */
object SetDSL {

  // create a logger instance
  val logger = CreateLogger(this.getClass)

  // globalScope - highest level of scope
  private val _NAME_ = "_NAME_"
  private val _PARENT_ = "_PARENT_"
  private val globalScope: mutable.Map[String, Any] = mutable.Map()
  globalScope.put(_NAME_, "global")
  globalScope.put(_PARENT_, None)

  // stringToMap - takes scopeName and gives the scopeMap
  private val stringToMap: mutable.Map[String, mutable.Map[String, Any]] = mutable.Map()

  // set current scope - will get updates whenever scope is changed
  // declared as private var and they cannot be accessed outside SetDSL object
  private var currScopeName = "global"
  private var currentScope = globalScope
  stringToMap.put(currScopeName, globalScope)

  // lookup table of all the variables available in a scope
  private val executionScope: mutable.Map[String, Any] = mutable.Map()

  /** Factory for [[Macro]] instances */
  object Macro {
    // signature 1 - Macro(String)
    def apply(name: String): Expression = {
      logger.info("Apply method of Macro with only name")
      executionScope.get(name) match {
        case Some(expression: Expression) => expression
        case None => Expression.Empty
      }
    }
    // signature 2 - Macro(String, Expression)
    def apply(name: String, expression: Expression): Unit = {
      logger.info("Apply method of Macro with name and expression")
      currentScope.put(name, expression)
      executionScope ++= currentScope
    }
  }

  /*
   * Expression type that encapsulates all the operations in the DSL
   * Expression can be of any of the following types
   * Value - Value(i) returns a Set(i)
   * Var - Var("x") returns the associated set if it is there in the scope
   * or creates an empty set and assigns to "x" in the scope
   * Insert, Delete, Update - Evaluates a sequence of Expression to a set
   * Assign - Assigns the evaluated set to a variable in the current scope
   * Scope - Create a new scope or toggle to the existing scope
   * SET OPERATIONS
   * Check - Check if an object exist in the set referenced by a variable
   * Union - Perform union of two sets
   * Intersection - Perform intersection of two sets
   * Difference - Perform difference between two sets
   * SymmetricDifference - Perform symmetric difference between two sets
   * CartesianProduct - Return pairwise (a,b) for all a in set A and b in set B
   * */
  enum Expression {
    case Value(input: Any)
    case Var(name: String)
    case Insert(expressions: Seq[Expression])
    case Assign(expression1: Expression, expression2: Expression)
    case Delete(expressions: Seq[Expression])
    case Update(expressions: Seq[Expression])
    case Scope(name: String, expression: Expression)
    case Check(variable: Var, value: Value)
    case Empty
    private case ExpressionsToSet(expressions: Seq[Expression])

    case Union(expression1: Expression, expression2: Expression)
    case Intersection(expression1: Expression, expression2: Expression)
    case Difference(expression1: Expression, expression2: Expression)
    case SymmetricDifference(expression1: Expression, expression2: Expression)
    case CartesianProduct(expression1: Expression, expression2: Expression)

    // evaluate() called by the Driver program
    // reset the state of the execution
    def evaluate(): HashSet[Any] ={
      executionScope.clear()
      executionScope ++= globalScope
      currentScope = globalScope
      currScopeName = "global"
      logger.info("Context reset, ready to evaluate...")
      this._evaluate()
    }

    protected def _evaluate(): HashSet[Any] = {
      this match {
        // value - returns set(value)
        case Value(i) => HashSet[Any](i)

        // var - if var exists in the map returns a set
        // otherwise create a new set for the var
        case Var(name: String) => {
          // check for the variable in the current scope
          // current scope has highest priority
          // other wise check in the current execution
          // context cache
          currentScope.contains(name) match {
            case true => currentScope(name).asInstanceOf[HashSet[Any]]
            case false => {
              executionScope.contains(name) match {
                case true => executionScope(name).asInstanceOf[HashSet[Any]]
                case false => {
                  HashSet[Any]()
//                  currentScope.put(name, HashSet[Any]())
//                  executionScope ++= currentScope
//                  currentScope(name).asInstanceOf[HashSet[Any]]
                }
              }
            }
          }
        }

        // assign and insert - insert the elements
        // to original set if it exists or create
        // a new set with the inserted elements
        case Assign(variable: Var, set: Insert) => {
          // evaluate the expressions
          val ret = variable._evaluate() ++ set._evaluate()
          // update the current state and the cache
          currentScope.put(variable.name, ret)
          executionScope ++= currentScope
          ret
        }

        // assign and value - evaluate the value and
        // assign it to the variable in the current scope
        case Assign(variable: Var, value: Value) => {
          val ret = variable._evaluate() ++ value._evaluate()
          currentScope.put(variable.name, ret)
          executionScope ++= currentScope
          ret
        }

        // assign and delete - delete the elements from
        // the set if the exist or throw an exception
        case Assign(variable: Var, set: Delete) => {
          // evaluate the expression
          val ret = variable._evaluate() -- set._evaluate()
          // recursive method to update the state of execution
          def _updateState(name: String, scope: mutable.Map[String, Any]): Unit = {
            scope.contains(name) match {
              case true => scope.put(name, ret)
              // if the variable doesn't exist in the current
              // scope recursively check all its parent scopes
              // and update the status
              case false => {
                val parent: String = scope(_PARENT_).toString
                if (parent != "None") {
                  val parentScope = stringToMap(parent)
                  _updateState(name, parentScope)
                } else {
                  throw new RuntimeException("Cannot delete a variable that doesn't exist in any scope")
                }
              }
            }
          }
          // call the updateState method with currentScope
          try {
            _updateState(variable.name, currentScope)
          } catch {
            case ex: RuntimeException => ex.printStackTrace()
          }

          // update the cache
          executionScope.put(variable.name, ret)
          ret
        }

        // assign and update - update the set if the exist
        // or throw an exception
        case Assign(variable: Var, set: Update) => {
          // evaluate the expression
          val ret = variable._evaluate() ++ set._evaluate()
          // recursive method to update the state
          def _updateState(name: String, scope: mutable.Map[String, Any]): Unit = {
            scope.contains(name) match {
              case true => scope.put(name, ret)
              case false => {
                // if the scope in question doesn't contain
                // the variable, recursively check all its
                // parent scopes and update the status
                val parent: String = scope(_PARENT_).toString
                if (parent != "None") {
                  val parentScope = stringToMap(parent)
                  _updateState(name, parentScope)
                } else {
                  throw new RuntimeException("Cannot update a variable that doesn't exist in any scope")
                }
              }
            }
          }
          //_updateState(variable.name, currentScope)
          // call the updateState method with currentScope
          try {
            _updateState(variable.name, currentScope)
          } catch {
            case ex: RuntimeException => ex.printStackTrace()
          }

          // update the cache
          executionScope.put(variable.name, ret)
          ret
        }

        // assign and expression - for evaluating macro
        // example - Assign(Var("x"), Var("y"))
        // other expressions are taken care of by any of
        // the above implementations of Assign
        case Assign(variable: Var, expression: Expression) => {
          val ret = expression._evaluate()
          currentScope.put(variable.name, ret)
          //println(searchScope)
          ret
        }

        // empty - return an empty set
        case Empty => HashSet[Any]()

        // convert a sequence of expression wrapped within insert or
        // delete statement to a set of values and return the set
        case ExpressionsToSet(expressions: Seq[Expression]) => {
          def recursive(head: Expression, tail: Seq[Expression]): HashSet[Any] = {
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

        // convert the expression within delete statement to
        // a set of values and return the set
        case Update(expressions: Seq[Expression]) => {
          ExpressionsToSet(expressions)._evaluate()
        }

        // check if an object exist in a scope
        case Check(variable: Var, value: Value) => {
          HashSet(variable._evaluate() contains value._evaluate().head)
        }

        // return union of set A evaluated by expression1
        // and set B evaluated by expression
        case Union(expression1: Expression, expression2: Expression) => {
          def _union(set1 : HashSet[Any], set2 : HashSet[Any]): HashSet[Any] = {
            set1.flatMap(a => set2 + a)
          }
          val exp1 = expression1._evaluate()
          val exp2 = expression2._evaluate()
          val ret = _union(expression1._evaluate(), expression2._evaluate())
          ret.asInstanceOf[HashSet[Any]]
        }

        // return intersection of set A evaluated by expression1
        // and set B evaluated by expression2
        case Intersection(expression1: Expression, expression2: Expression) => {
          val ret = expression1._evaluate() intersect expression2._evaluate()
          ret
        }

        // return difference between set A evaluated by expression1
        // and set B evaluated by expression2
        case Difference(expression1: Expression, expression2: Expression) => {
          expression1._evaluate() diff expression2._evaluate()
        }

        // return symmetric difference between set A evaluated
        // by expression1 and set B evaluated by expression2
        case SymmetricDifference(expression1: Expression, expression2: Expression) => {
          val set1 = expression1._evaluate()
          val set2 = expression2._evaluate()
          val union = set1 ++ set2
          val intersect = set1 intersect set2
          println("Set1 " + set1)
          println("Set2 " + set2)
          println("Union " + union)
          println("Intersection " + intersect)
          union diff intersect
        }

        // return pairwise (a, b) for all a, b in set A and set B
        // evaluated by expression1 and expression2
        case CartesianProduct(expression1: Expression, expression2: Expression) => {
          val set1 = expression1._evaluate()
          val set2 = expression2._evaluate()
          set1.flatMap(element1 => set2.map(element2 => (element1, element2)))
        }

        // create a new scope or update the current scope
        case Scope(name: String, expression: Expression) => {
          if (!currentScope.contains(name)) {
            val newScope = mutable.Map[String, Any]()
            newScope.put(_NAME_, name)
            newScope.put(_PARENT_, currScopeName)
            currentScope.put(name, newScope)
            stringToMap.put(name, newScope)
          }
          executionScope ++= currentScope
          currentScope = currentScope(name).asInstanceOf[mutable.Map[String, Any]]
          currScopeName = name
          expression._evaluate()
        }
      }
    }
  }
}

