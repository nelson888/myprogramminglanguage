package com.tambapps.compiler.analyzer.token

import com.tambapps.compiler.util.Symbol

class TokenUtils {

  static final int LEFT = 0
  static final int RIGHT = 1

  static final Map<String, TokenType> KEYWORDS_MAP
  static final Map<String, TokenType> SYMBOLS_MAP

  static final Map<TokenType, TokenNodeType> UNARY_OPERATOR_MAP
  static final Map<TokenType, TokenNodeType> BINARY_OPERATOR_MAP
  static final Map<TokenNodeType, Closure> OPERATOR_MAP
  static final Map<TokenType, TokenNodeType> TYPE_MAP
  static final Map<TokenType, Integer> PRIORITY_MAP
  static final Map<TokenType, Integer> ASSOCIATIVITY_MAP
  static final Map<TokenType, Symbol.Type> VAR_TYPE_MAP

  static {
    Map<TokenType, TokenNodeType> unaryMap = new HashMap<>()
    unaryMap.put(TokenType.PLUS, TokenNodeType.PLUS_U)
    unaryMap.put(TokenType.MINUS, TokenNodeType.MINUS_U)
    unaryMap.put(TokenType.NOT, TokenNodeType.NOT)
    unaryMap.put(TokenType.MULTIPLY, TokenNodeType.D_REF)

    Map<TokenType, TokenNodeType> binaryMap = new HashMap<>()
    binaryMap.put(TokenType.PLUS, TokenNodeType.PLUS_B)
    binaryMap.put(TokenType.MINUS, TokenNodeType.MINUS_B)
    binaryMap.put(TokenType.MODULO, TokenNodeType.MODULO)
    binaryMap.put(TokenType.MULTIPLY, TokenNodeType.MULTIPLY)
    binaryMap.put(TokenType.DIVIDE, TokenNodeType.DIVIDE)
    binaryMap.put(TokenType.POWER, TokenNodeType.POWER)
    binaryMap.put(TokenType.EQUAL, TokenNodeType.EQUAL)
    binaryMap.put(TokenType.NOT_EQUAL, TokenNodeType.NOT_EQUAL)
    binaryMap.put(TokenType.STRICT_SUP, TokenNodeType.STRICT_SUP)
    binaryMap.put(TokenType.STRICT_INF, TokenNodeType.STRICT_INF)
    binaryMap.put(TokenType.SUP, TokenNodeType.SUP)
    binaryMap.put(TokenType.INF, TokenNodeType.INF)
    binaryMap.put(TokenType.AND, TokenNodeType.AND)
    binaryMap.put(TokenType.OR, TokenNodeType.OR)
    binaryMap.put(TokenType.ASSIGNMENT, TokenNodeType.ASSIGNMENT)

    Map<TokenType, TokenNodeType> typeMap = new HashMap<>()
    typeMap.put(TokenType.IDENTIFIER, TokenNodeType.VAR_REF)
    typeMap.put(TokenType.IF, TokenNodeType.COND)
    typeMap.put(TokenType.ACCOLADE_OPEN, TokenNodeType.BLOC)
    typeMap.put(TokenType.WHILE, TokenNodeType.LOOP)
    typeMap.put(TokenType.FOR, TokenNodeType.SEQ)
    typeMap.put(TokenType.PRINT, TokenNodeType.PRINT)
    typeMap.put(TokenType.RETURN, TokenNodeType.RETURN)
    typeMap.put(TokenType.BREAK, TokenNodeType.BREAK)
    typeMap.put(TokenType.QUESTION_MARK, TokenNodeType.TERNARY)
    typeMap.put(TokenType.CONTINUE, TokenNodeType.CONTINUE)
    typeMap.put(TokenType.INT, TokenNodeType.INT)
    typeMap.put(TokenType.FLOAT, TokenNodeType.FLOAT)
    typeMap.put(TokenType.CHAR, TokenNodeType.CHAR)
    typeMap.put(TokenType.STRING, TokenNodeType.STRING)
    typeMap.put(TokenType.TRUE, TokenNodeType.INT)
    typeMap.put(TokenType.FALSE, TokenNodeType.INT)
    typeMap.put(TokenType.SWITCH, TokenNodeType.SWITCH)
    typeMap.put(TokenType.CASE, TokenNodeType.CASE)
    typeMap.put(TokenType.DEFAULT, TokenNodeType.CASE)
    typeMap.put(TokenType.CONST, TokenNodeType.CONST_DECL)

    Map<TokenType, Integer> priorityMap = new HashMap<>()
    priorityMap.put(TokenType.POWER, 1)
    priorityMap.put(TokenType.NOT, 1)
    priorityMap.put(TokenType.MULTIPLY, 2)
    priorityMap.put(TokenType.DIVIDE, 2)
    priorityMap.put(TokenType.MODULO, 2)
    priorityMap.put(TokenType.PLUS, 3)
    priorityMap.put(TokenType.MINUS, 3)
    priorityMap.put(TokenType.AND, 4)
    priorityMap.put(TokenType.EQUAL, 5)
    priorityMap.put(TokenType.NOT_EQUAL, 5)
    priorityMap.put(TokenType.STRICT_SUP, 5)
    priorityMap.put(TokenType.STRICT_INF, 5)
    priorityMap.put(TokenType.SUP, 5)
    priorityMap.put(TokenType.INF, 5)
    priorityMap.put(TokenType.OR, 5)
    priorityMap.put(TokenType.ASSIGNMENT, 6)

    def operatorMap = new HashMap<TokenNodeType, Closure>()
    operatorMap.put(TokenNodeType.PLUS_B, {a1, a2 -> return a1 + a2 })
    operatorMap.put(TokenNodeType.MULTIPLY, {a1, a2 -> return a1 * a2 })
    operatorMap.put(TokenNodeType.MODULO, {a1, a2 -> return a1 % a2 })
    operatorMap.put(TokenNodeType.DIVIDE, {a1, a2 -> return a1 / a2 })
    operatorMap.put(TokenNodeType.POWER, {a1, a2 -> return a1 ** a2 })
    operatorMap.put(TokenNodeType.MINUS_B, {a1, a2 -> return a1 - a2 })

    operatorMap.put(TokenNodeType.EQUAL, {a1, a2 -> return intBool(a1 == a2) })
    operatorMap.put(TokenNodeType.NOT_EQUAL, {a1, a2 -> return intBool(a1 != a2) })
    operatorMap.put(TokenNodeType.STRICT_INF, {a1, a2 -> return intBool(a1 < a2) })
    operatorMap.put(TokenNodeType.STRICT_SUP, {a1, a2 -> return intBool(a1 > a2) })
    operatorMap.put(TokenNodeType.SUP, {a1, a2 -> return intBool(a1 >= a2) })
    operatorMap.put(TokenNodeType.INF, {a1, a2 -> return intBool(a1 <= a2) })
    operatorMap.put(TokenNodeType.AND, {a1, a2 -> return intBool(a1 && a2) })
    operatorMap.put(TokenNodeType.OR, {a1, a2 -> return intBool(a1 || a2) })

    operatorMap.put(TokenNodeType.MINUS_U, {a -> return - a })
    operatorMap.put(TokenNodeType.PLUS_U, {a -> return a })
    operatorMap.put(TokenNodeType.NOT, {a -> return intBool(!a) })

    operatorMap.put(TokenNodeType.INCREMENT_AFTER, {a -> return a + 1 }) //increment regarless of order (after/before). Order is
    operatorMap.put(TokenNodeType.INCREMENT_BEFORE, operatorMap.get(TokenNodeType.INCREMENT_AFTER)) //handled in Evaluator
    operatorMap.put(TokenNodeType.DECREMENT_AFTER, {a -> return a - 1 })
    operatorMap.put(TokenNodeType.DECREMENT_BEFORE, operatorMap.get(TokenNodeType.DECREMENT_AFTER))

    //filled in the loop
    Map<TokenType, Integer> associativityMap = new HashMap<>()
    Map<String, TokenType> keywordsMap = new HashMap<>()
    Map<String, TokenType> symbolsMap = new HashMap<>()

    for (TokenType t : TokenType.values()) {
      if (t.isSymbol()) {
        symbolsMap.put(t.value, t)
      } else if (t.isKeyWord()) {
        keywordsMap.put(t.value, t)
      }
      if (t.isBinaryOperator()) {
        associativityMap.put(t, t == TokenType.POWER || t == TokenType.ASSIGNMENT ? RIGHT : LEFT)
      }
    }

    def varTypeMap = new HashMap<TokenType, Symbol.Type>()
    varTypeMap.put(TokenType.TYPE_STRING, Symbol.Type.STRING)
    varTypeMap.put(TokenType.TYPE_INT, Symbol.Type.INT)
    varTypeMap.put(TokenType.TYPE_BOOL, Symbol.Type.INT)
    varTypeMap.put(TokenType.TYPE_FLOAT, Symbol.Type.FLOAT)
    varTypeMap.put(TokenType.TYPE_CHAR, Symbol.Type.CHAR)
    varTypeMap.put(TokenType.VAR, Symbol.Type.ANY)

    UNARY_OPERATOR_MAP = unaryMap.asImmutable()
    BINARY_OPERATOR_MAP = binaryMap.asImmutable()
    TYPE_MAP = typeMap.asImmutable()
    KEYWORDS_MAP = keywordsMap.asImmutable()
    SYMBOLS_MAP = symbolsMap.asImmutable()
    PRIORITY_MAP = priorityMap.asImmutable()
    ASSOCIATIVITY_MAP = associativityMap.asImmutable()
    OPERATOR_MAP = operatorMap.asImmutable()
    VAR_TYPE_MAP = varTypeMap.asImmutable()
  }


  private static int intBool(boolean b) {
    return b ? 1 : 0
  }

}
