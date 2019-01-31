package com.tambapps.compiler.exception

import com.tambapps.compiler.analyzer.token.TokenNode
import com.tambapps.compiler.analyzer.token.TokenNodeType
import com.tambapps.compiler.util.Symbol.Type

class NoSuchOperatorException extends EvaluationException {

  NoSuchOperatorException(TokenNodeType operator, Type type1, Type type2, TokenNode n) {
    super("There is no operator $operator for arguments $type1, $type2", n.l, n.c)
  }

  NoSuchOperatorException(TokenNodeType operator, Type type, TokenNode n) {
    super("There is no operator $operator for arguments $type", n.l, n.c)
  }
}
