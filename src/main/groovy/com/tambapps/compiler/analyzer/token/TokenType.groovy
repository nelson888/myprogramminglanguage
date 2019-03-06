package com.tambapps.compiler.analyzer.token

import com.tambapps.compiler.util.Array
import com.tambapps.compiler.util.Symbol

import java.util.stream.Stream

enum TokenType {
  //removable tokens
  WHITESPACE('[ ]+'),
  LINE_BREAK('\\R'),
  COMMENT_LINE('//(.*)\n'),
  COMMENT_BLOCK('/\\*.*\n.*\\*\\/'),

  //operators
  NOT('!'),// unary operator
  QUESTION_MARK('\\?'), COLON(':'),
  INCREMENT('\\+\\+'), DECREMENT('\\-\\-'),
  PLUS('\\+'), MINUS('-'), // unary or binary operator
  DIVIDE('/'), MULTIPLY('\\*'), POWER('\\*\\*'), MODULO('%'), // binary operator
  EQUAL('=='), NOT_EQUAL('!='), STRICT_SUP('>'), STRICT_INF('<'), SUP('>='), INF('<='), AND('and'), OR('or'), //binary operator
  ASSIGNMENT('='),

  //other symbols
  PARENT_OPEN('\\('), PARENT_CLOSE('\\)'), COMMA(','), SEMICOLON(';'), ACCOLADE_OPEN('\\{'),
  ACCOLADE_CLOSE('\\}'), BRACKET_OPEN('\\['), BRACKET_CLOSE('\\]'),

  //key words
  IF('if'), ELSE('else'), FOR('for'), WHILE('while'), PRINT('print'), RETURN('return'),
  BREAK('break'), CONTINUE('continue'), VAR('var'), TYPE_STRING('string'), TYPE_CHAR('char'),
  TYPE_INT('int'), TYPE_FLOAT('float'), TYPE_BOOL('bool'), SWITCH('switch'), CASE('case'),
  DEFAULT('default'), CONST('const'),

  //values
  STRING('\"(.*?)\"', {String v -> new Array(Symbol.Type.CHAR, v.substring(1, v.length() - 1)) }),
  CHAR("'(.*?)'", {String v -> v.charAt(1)}),
  FLOAT('-?([0-9]+\\.[0-9]*)|([0-9]*\\.[0-9]+)', Float.&parseFloat),
  INT('-?[0-9]+', Integer.&parseInt),
  TRUE('true', { v -> true }), FALSE('false', { v -> false }),

  IDENTIFIER('[a-zA-Z0-9_]+', { v -> v}),
  END_OF_FILE('EOF');

  final String value
  private final Closure matchingMapper

  TokenType(String value) {
    this(value, null)
  }

  TokenType(String value, Closure matchingMapper) {
    this.value = value
    this.matchingMapper = matchingMapper
  }

  boolean isBinaryOperator() {
    return this in [PLUS, MINUS, DIVIDE, MULTIPLY, POWER, MODULO, EQUAL, NOT_EQUAL, STRICT_SUP, STRICT_INF, SUP, INF, AND, OR, ASSIGNMENT]
  }

  boolean isBooleanOperator() {
    return this in [EQUAL, NOT_EQUAL, STRICT_SUP, STRICT_INF, SUP, INF, AND, OR, NOT]
  }

  boolean isOperator() {
    return Stream.of(PLUS, MINUS, DIVIDE, MULTIPLY, POWER, MODULO, NOT, ASSIGNMENT, EQUAL, NOT_EQUAL, STRICT_SUP, STRICT_INF, SUP, INF)
        .anyMatch(this.&equals)
  }

  boolean isSymbol() {
    return value?.chars()?.allMatch({ c -> !Character.isLetter(c) })
  }

  boolean isKeyWord() {
    return value?.chars()?.allMatch(Character.&isLetter)
  }

  boolean isRemovable() {
    return this in [WHITESPACE, COMMENT_BLOCK, COMMENT_LINE, LINE_BREAK]
  }

  def matchMap(String match) {
    return matchingMapper ? matchingMapper(match) : null
  }
  /**
   * returns wether this type can only be a single char symbol
   * e.g: '^' is only a single char symbol
   * but '<' is not because there is the symbol '<=' that contains more than 1 character
   * @return
   */
  boolean isOnlySingleCharSymbol() {
    return isSymbol() && value.length() == 1 && !Stream.of(ASSIGNMENT, STRICT_SUP, STRICT_INF, NOT).anyMatch(this.&equals)
  }

  boolean isVarType() {
    return this in [TYPE_FLOAT, TYPE_INT, TYPE_STRING, TYPE_CHAR, VAR]
  }
}