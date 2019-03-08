package com.tambapps.compiler.eval.console

import com.tambapps.compiler.util.Symbol

//TODO allow function overloading
class CFunction {

  final String name
  final List<Symbol.Type> argTypes
  Closure closure //may be redefined
  final String description

  CFunction(String name, List<Symbol.Type> argTypes, Closure closure, String description) {
    this.name = name
    this.argTypes = argTypes.asImmutable()
    this.closure = closure
    this.description = description
  }

  CFunction(String name, List<Symbol.Type> argTypes, Closure closure) {
    this(name, argTypes, closure, null)
  }

  def call(Object[] args) {
    return closure.call(args)
  }

  boolean equals(o) {
    if (this.is(o)) return true
    if (getClass() != o.class) return false

    CFunction cFunction = (CFunction) o

    if (argTypes != cFunction.argTypes) return false
    if (name != cFunction.name) return false

    return true
  }

  int hashCode() {
    int result
    result = (name != null ? name.hashCode() : 0)
    result = 31 * result + (argTypes != null ? argTypes.hashCode() : 0)
    return result
  }
}
