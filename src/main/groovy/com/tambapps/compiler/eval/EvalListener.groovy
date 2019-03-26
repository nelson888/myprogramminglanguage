package com.tambapps.compiler.eval

import com.tambapps.compiler.util.Symbol

interface EvalListener {

  void onVarDecl(Symbol.Type type, String name, def value)

  void onVarAssign(Symbol.Type type, String name, def value)

}
