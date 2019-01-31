package com.tambapps.compiler.util

import com.tambapps.compiler.analyzer.token.TokenNodeType
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
    STRING("", TokenNodeType.STRING), CHAR(' ' as Character, TokenNodeType.CHAR),
    INT(0, TokenNodeType.INT), FLOAT(0f, TokenNodeType.FLOAT), ANY(0, null),
    FUNCTION(null, null) //TODO? function as variable?

    final def defaultValue
    private final TokenNodeType nodeType

    Type(def defaultValue, TokenNodeType nodeType) {
      this.defaultValue = defaultValue
      this.nodeType = nodeType
    }

    boolean isType(def value) {
      if (this == ANY) return true
      switch (value.getClass()) {
        case Integer:
        case Character:
          return this in [INT, CHAR]
        case Float:
          return this == FLOAT
        case String:
          return this == STRING
      }
      return false
    }

    boolean isNumber() {
      return this in [INT, CHAR, FLOAT]
    }

    boolean isNode(TokenNodeType type) {
      return !nodeType || nodeType == type
    }

    static Type toType(def value) {
      switch (value.getClass()) {
        case Integer:
          return INT
        case String:
          return STRING
        case Character:
          return CHAR
        case Float:
          return FLOAT
      }
    }
  }
}
