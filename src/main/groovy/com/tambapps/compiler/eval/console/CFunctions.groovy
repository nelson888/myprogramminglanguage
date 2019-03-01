package com.tambapps.compiler.eval.console

import static com.tambapps.compiler.eval.Evaluator.VOID

import com.tambapps.compiler.eval.Evaluator

final class CFunctions {

  private CFunctions() {}


  protected static List<CFunction> getAll(Evaluator evaluator) {
    def listVariables = { evaluator.allSymbols.findAll { !it.function }.each { println("$it.type $it.ident: $it.value")}; VOID } //returns nothing
    def listFunctions = { evaluator.allSymbols.findAll { it.function }.each { println("$it.ident: $it.nbArgs")}; VOID } //TODO get args types in functions list
    return [
        new CFunction("listVars", [], listVariables),
        new CFunction("listFuncs", [], listFunctions)
    ]

  }
}
