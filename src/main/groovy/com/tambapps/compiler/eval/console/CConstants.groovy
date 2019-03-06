package com.tambapps.compiler.eval.console

import com.tambapps.compiler.util.Symbol

class CConstants {

  static void injectConstant(Closure consumer) {
    for (Symbol.Type type : Symbol.Type.values()) {
      consumer("const string $type = \"$type\";")
    }
  }
}
