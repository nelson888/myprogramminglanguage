package com.tambapps.compiler.exception

import groovy.transform.PackageScope

class EvaluationException extends RuntimeException {

  EvaluationException(String message, int l, int c) {
    super("At l:$l c:$c: " + message)
  }

  //for symbol exceptions
  @PackageScope
  EvaluationException(String var1) {
    super(var1)
  }
}
