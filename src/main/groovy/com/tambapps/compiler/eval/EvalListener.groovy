package com.tambapps.compiler.eval

interface EvalListener {

  void onVarDecl(String name, def value)

  void onVarAssign(String name, def value)

}
