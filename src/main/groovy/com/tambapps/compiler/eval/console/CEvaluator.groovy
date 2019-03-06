package com.tambapps.compiler.eval.console

import com.tambapps.compiler.analyzer.token.TokenNode
import com.tambapps.compiler.eval.Evaluator
import com.tambapps.compiler.exception.EvaluationException
import com.tambapps.compiler.exception.WrongTypeException
import com.tambapps.compiler.util.Symbol

class CEvaluator extends Evaluator {

  private final List<CFunction> cFunctions

  CEvaluator(List<TokenNode> functions, List<CFunction> cFunctions, Closure printer) {
    super(functions, printer)
    this.cFunctions = cFunctions
  }

  @Override
  def functionCall(TokenNode e) {
    String name = e.value.name
    TokenNode function = functions.find { name == it.value.name }
    if (function != null) { // function not found
      return functionCall(e, function)
    }
    CFunction cFunction = cFunctions.find { name == it.name }
    if (cFunction != null) {
      return cFunctionCall(cFunction, e)
    }
    throw new EvaluationException("Couldn't find function $name", e.l, e.c)
  }

  def cFunctionCall(CFunction function, TokenNode argsNode) {
    int nbArgs = function.argTypes.size()
    int nbChildren = argsNode.nbChildren()
    if (nbChildren != nbArgs) {
      throwArgsCountException(argsNode, nbArgs, nbChildren)
    }

    List<Object> argValues = []
    for (int i = 0; i < nbArgs; i++) {
      Symbol.Type type = function.argTypes[i]
      def argValueNode = argsNode.getChild(i)
      def argValue = evaluate(argValueNode)
      if (!type.isType(argValue)) {
        throw new WrongTypeException(type, argValue, argValueNode)
      }
      argValues += argValue
    }
    return function.closure.call(*argValues)
  }
}
