package com.tambapps.compiler.exception

import com.tambapps.compiler.analyzer.token.TokenNode

class IllegalStatementException extends EvaluationException {

  IllegalStatementException(String message, TokenNode n) {
    super(message, n.l, n.c)
  }

}
