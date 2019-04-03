package com.tambapps.compiler.analyzer.token

import com.tambapps.compiler.util.Symbol.Type

enum TokenNodeType {
  PLUS_U, MINUS_U, NOT,
  INCREMENT_BEFORE, DECREMENT_BEFORE, INCREMENT_AFTER, DECREMENT_AFTER,
  PLUS_B, MINUS_B, MODULO, MULTIPLY, DIVIDE, POWER,
  TERNARY,
  EQUAL, NOT_EQUAL, STRICT_INF, STRICT_SUP, SUP, INF, AND, OR,
  DROP, ASSIGNMENT, VAR_REF, VAR_DECL, CONST_DECL,
  COND, BREAK, CONTINUE, LOOP, SWITCH, CASE,
  PROG, BLOC, SEQ, FUNCTION, FUNCTION_CALL, RETURN,D_REF,
  PRINT,
  TAB_DECL, TAB_REF,
  INT, CHAR, STRING, FLOAT, ARRAY;

  boolean isUnaryOperator() {
    return this in [PLUS_U, MINUS_U, NOT]
  }

  boolean isBinaryOperator() {
    return this in [PLUS_B, MINUS_B, MODULO, MULTIPLY, DIVIDE, POWER,
                    EQUAL, NOT_EQUAL, STRICT_INF, STRICT_SUP, SUP, INF, AND, OR]
  }

  boolean isOperator() {
    return isBinaryOperator() || isUnaryOperator() || this in [INCREMENT_BEFORE, DECREMENT_BEFORE, INCREMENT_AFTER, DECREMENT_AFTER]
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
    return true
  }

  boolean canOperate(Type arg) {
    return arg != Type.STRING
  }

  boolean isSimpleValue() {
    return this in [INT, CHAR, STRING, FLOAT]
  }

  boolean isValue() {
    return simpleValue || this == ARRAY
  }

  @Override
  String toString() {
    String name = name()
    if (name.contains("_U")) {
      return "UNARY " + name.replace("_U", "")
    } else if (name.contains("_U")) {
      return "BINARY " + name.replace("_B", "")
    } else {
      return name
    }
  }
}
