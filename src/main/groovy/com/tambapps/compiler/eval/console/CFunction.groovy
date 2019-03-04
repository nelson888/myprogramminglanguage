package com.tambapps.compiler.eval.console

import com.tambapps.compiler.util.Symbol

class CFunction {

  final String name
  final List<Symbol.Type> argTypes
  final Closure closure
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

  def call(def... args) {
    return closure.call(args)
  }


}
