package com.tambapps.compiler.exception

import com.tambapps.compiler.analyzer.token.TokenNode
import com.tambapps.compiler.util.Symbol.Type

class WrongTypeException extends EvaluationException {

  WrongTypeException(String message, int l, int c) {
    super(message, l, c)
  }

  WrongTypeException(Type expected, def value, TokenNode tk) {
    super("Expected expression of type $expected but got ${Type.fromValue(value)}", tk.l, tk.c)
  }

  WrongTypeException(Type expected, TokenNode tk) {
    super("Expected expression of type $expected", tk.l, tk.c)
  }

}
