package com.tambapps.compiler.exception

import com.tambapps.compiler.analyzer.token.TokenNode
import com.tambapps.compiler.util.Symbol.Type as Type
class WrongTypeException extends CompileException {

  WrongTypeException(Type expected, def value, TokenNode tk) {
    super("Expected expression of type $expected but got ${Type.toType(value)}", tk.l, tk.c)
  }

  WrongTypeException(Type expected, TokenNode tk) {
    super("Expected expression of type $expected", tk.l, tk.c)
  }

}
