package com.tambapps.compiler.util

import groovy.transform.PackageScope

class Symbol {

  String ident
  int slot
  int nbArgs = -1
  Type type
  def value //for evaluator

  @PackageScope
  Symbol(String s) {
    ident = s
  }

  @PackageScope
  Symbol(String s, int nbArgs) {
    ident = s
    this.nbArgs = nbArgs
  }

  private Symbol(String ident, int slot, int nbArgs, int value) {
    this.ident = ident
    this.slot = slot
    this.nbArgs = nbArgs
    this.value = value
  }

  boolean isFunction() {
    return nbArgs >= 0
  }

  Symbol copy() {
    return new Symbol(ident, slot, nbArgs, value)
  }

  enum Type {
    STRING(""), CHAR(' ' as Character), INT(0), FLOAT(0f), ANY(0),
    FUNCTION(null); //TODO? function as variable?

    final def defaultValue

    Type(def defaultValue) {
      this.defaultValue = defaultValue
    }
  }
}
