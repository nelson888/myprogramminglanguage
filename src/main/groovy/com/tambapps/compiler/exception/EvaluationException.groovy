package com.tambapps.compiler.exception

class EvaluationException extends RuntimeException {

  EvaluationException(String message, int l, int c) {
    super("At l:$l c:$c: " + message)
  }

}
