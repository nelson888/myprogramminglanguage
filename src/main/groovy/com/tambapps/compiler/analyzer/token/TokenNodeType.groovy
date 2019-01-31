package com.tambapps.compiler.analyzer.token

import com.tambapps.compiler.util.Symbol.Type

enum TokenNodeType {
  PLUS_U, MINUS_U, NOT,
  PLUS_B, MINUS_B, MODULO, MULTIPLY, DIVIDE, POWER,
  EQUAL, NOT_EQUAL, STRICT_INF, STRICT_SUP, SUP, INF, AND, OR,
  DROP, ASSIGNMENT, VAR_REF, VAR_DECL,
  COND, BREAK, LOOP,
  PROG, BLOC, SEQ, FUNCTION, FUNCTION_CALL, RETURN,D_REF,
  PRINT,
  TAB_DECL, TAB_REF,
  INT, CHAR, STRING, FLOAT;

  boolean isUnaryOperator() {
    return this in [PLUS_U, MINUS_U, NOT]
  }

  boolean isBinaryOperator() {
    return this in [PLUS_B, MINUS_B, MODULO, MULTIPLY, DIVIDE, POWER,
                    EQUAL, NOT_EQUAL, STRICT_INF, STRICT_SUP, SUP, INF, AND, OR]
  }

  boolean canOperate(Type arg1, Type arg2) {
    switch (this) {
      case PLUS_B:
        return true
      case MINUS_B:
        return arg1 == arg2 || arg1.number && arg2.number
      case MODULO:
        return arg1 == Type.INT && arg2 == Type.INT
      case MULTIPLY:
        return arg1.number && arg2.number || arg1 == Type.STRING && arg2.number
    }
    return false
  }

  boolean canOperate(Type arg1) {
    switch (this) {
      case PLUS_B:
        return arg1 != Type.STRING
      case MINUS_B:
        return ! (arg1 in [Type.STRING, Type.CHAR])
    }
    return false
  }

  boolean isValue() {
    return this in [INT, CHAR, STRING, FLOAT]
  }

  boolean isStringOperator() {
    return isBinaryOperator() && ! (this in [MODULO, DIVIDE, MULTIPLY, POWER])
  }

  @Override
  String toString() {
    return name()
        .replace("_U", " UNARY")
        .replace("_B", " BINARY")

  }
}
