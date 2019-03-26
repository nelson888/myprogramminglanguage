package com.tambapps.compiler.eval.console

import com.tambapps.compiler.analyzer.token.TokenNode
import com.tambapps.compiler.eval.Evaluator
import com.tambapps.compiler.exception.EvaluationException
import com.tambapps.compiler.util.Symbol

class CEvaluator extends Evaluator {

  private final Collection<CFunction> cFunctions

  CEvaluator(List<TokenNode> functions, Collection<CFunction> cFunctions, Closure printer) {
    super(functions, printer)
    this.cFunctions = cFunctions.asUnmodifiable()
  }

  @Override
  def functionCall(TokenNode e) {
    String name = e.value.name
    TokenNode function = functions.find { name == it.value.name }
    if (function != null) { // function not found
      return functionCall(e, function)
    }
    List argValues = e.childrenIterator().collect(this.&evaluate)

    List<Symbol.Type> argTypes = argValues.collect(Symbol.Type.&fromValue)

    Collection<CFunction> cFunctions = cFunctions.findAll { name == it.name }
    if (!cFunctions) {
      throw new EvaluationException("There isn't a function $name", e.l, e.c)
    }
    CFunction cFunction = cFunctions.find { equivalentTypes(argTypes, it.argTypes) }

    if (cFunction == null) {
      throw new EvaluationException("Can't call function $name with args $argTypes", e.l, e.c)
    }

    return cFunction.closure.call(*argValues)
  }

  private static boolean equivalentTypes(List<Symbol.Type> types1, List<Symbol.Type> types2) {
    if (types1.size() != types2.size()) {
      return false
    }
    for (int i = 0; i < types1.size(); i++) {
      if (types1[i].compatible(types2[i])) {
        return false
      }
    }
    return true
  }
}
