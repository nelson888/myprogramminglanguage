package com.tambapps.compiler.eval.console

import static com.tambapps.compiler.eval.Evaluator.VOID

import com.tambapps.compiler.eval.Evaluator
import com.tambapps.compiler.util.Symbol.Type

final class CFunctions {

  private CFunctions() {}

  protected static List<CFunction> getAll(Evaluator evaluator) {
    def listVariables = { evaluator.allSymbols.findAll { !it.function }.each { println("$it.type $it.ident: $it.value")}; VOID } //returns nothing
    def listFunctions = { evaluator.allSymbols.findAll { it.function }.each { println("$it.ident: $it.nbArgs")}; VOID } //TODO get args types in functions list
    def clearVars = { evaluator.clear(true, false); VOID }
    def clearFuncs = { evaluator.clear(false, true); VOID }
    def clearAll = { evaluator.clear(true, true); VOID }
    return [
        new CFunction("listVars", [], listVariables),
        new CFunction("listFuncs", [], listFunctions),
        new CFunction("clearVars", [], clearVars),
        new CFunction("clearFuncs", [], clearFuncs),
        new CFunction("clearAll", [], clearAll),
        new CFunction("typeof", [Type.ANY], { def t = Type.fromValue(it); return t ?: "UNKNOWN" }, "get the type of a variable")
    ]

  }
}
