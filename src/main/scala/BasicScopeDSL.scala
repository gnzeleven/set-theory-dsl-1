package org.cs474.setdsl

object BasicScopeDSL {
  type BasicType = Any
  enum Expression {
    case Value(input: BasicType)
    case Var(name: String, scope: String)
    case Assign(variable: Var, value: Value, scope: String)
    case Scope(name: String, expression: Expression)
    private val globalScoping: Map[String, Any] = Map()
    private val scopeParent: Map[String, String] = Map()
    private val stringToScope: Map[String, Map[String, Any]] = Map("global" -> globalScoping)
    private var currentScope: String = "global"

    def evaluate(): BasicType = {
      this match {
        case Value(i) => i
        case Var(name: String, scope: String) => {
          val scoping = stringToScope(scope)
          scoping.get(name) match {
            case Some(v) => v
            case _ => {
              def recursivelyCheck(scoping: Map[String, Any]): Any = {
                scoping.keys
                  .filter(_.startsWith("scope_"))
                  .foreach(key => {
                    val innerScope = stringToScope(key)
                    innerScope.get(name) match {
                      case Some(v) => v
                      case _ => recursivelyCheck(innerScope)
                    }
                  })
              }
              recursivelyCheck(scoping)
            }
          }
        }

        case Assign(variable: Var, value: Value, scope: String) => {

        }

        case Scope(name: String, expression: Expression) => {

        }
      }
    }
  }
}
