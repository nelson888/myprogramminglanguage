package com.tambapps.compiler.eval.console

import com.tambapps.compiler.util.Symbol

class CFunction {

  final String name
  final List<Symbol.Type> argTypes
  final Closure closure

  CFunction(String name, List<Symbol.Type> argTypes, Closure closure) {
    this.name = name
    this.argTypes = argTypes.asImmutable()
    this.closure = closure
  }

  def call(def... args) {
    return closure.call(args)
  }


}
