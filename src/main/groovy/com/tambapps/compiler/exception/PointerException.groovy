package com.tambapps.compiler.exception

import com.tambapps.compiler.analyzer.token.TokenNode

class PointerException extends EvaluationException {

  PointerException(String var1, TokenNode n) {
    super(var1, n.l, n.c)
  }

}
