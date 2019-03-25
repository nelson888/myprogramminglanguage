package com.tambapps.compiler.util

import com.tambapps.compiler.analyzer.token.TokenNodeType
import groovy.transform.PackageScope

class Symbol {

  String ident
  int slot
  int nbArgs = -1
  Type type
  def value //for evaluator
  boolean constVar = false

  @PackageScope
  Symbol(String s) {
    ident = s
  }

  @PackageScope
  Symbol(String s, int nbArgs) {
    ident = s
    this.nbArgs = nbArgs
  }

  private Symbol(String ident, int slot, int nbArgs, def value) {
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
    ARR_INT([], TokenNodeType.ARRAY),
    ARR_FLOAT([], TokenNodeType.ARRAY), //arr_char is string
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
        case Array:
          return value.type == arrElementType()
        case Integer:
          if (this == FLOAT) return true
        case Character:
          return this in [INT, CHAR]
        case Boolean:
          return this == INT
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

    static Type fromValue(def value) {
      switch (value.getClass()) {
        case Array:
          return arrayType(value.type)
        case Integer:
        case Boolean:
          return INT
        case String:
          return STRING
        case Character:
          return CHAR
        case Float:
          return FLOAT
      }
    }

    boolean isArrayType() {
      return isAny() || this == STRING || name().startsWith("ARR_")
    }

    boolean isAny() {
      return this == ANY
    }

    Type arrElementType() {
      if (!isArrayType()) return null
      if (isAny()) return ANY
      if (this == STRING) return CHAR
      return valueOf(name().replace("ARR_", ""))
    }

    static Type arrayType(Type t) {
      if (t == CHAR) {
        return STRING
      } else if (this == ANY) {
        return ANY
      }
      return valueOf("ARR_" + t.name())
    }

    boolean compatible(Type t) {
      return ANY.is(t) || ANY.is(this) || this.is(t)
    }

    Type elementType() {
      if (this == STRING) {
        return CHAR
      }
      if (isAny()) {
        return ANY
      }
      String name = name()
      if (!name.startsWith("ARR_")) {
        return null
      }
      return valueOf(name.replace("ARR_", ""))
    }
  }

  void setValue(def value) {
    if (type == Type.INT && value instanceof Character) {
      this.value = (int) value
    } else if (type == Type.CHAR && value instanceof Integer) {
      this.value =  (char) value
    } else {
      this.value = value
    }
  }
}
