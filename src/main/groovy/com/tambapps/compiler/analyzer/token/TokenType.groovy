package com.tambapps.compiler.analyzer.token

import java.util.stream.Stream

enum TokenType {
  NOT('!'),// unary operator
  PLUS('\\+'), MINUS('-'), // unary or binary operator
  DIVIDE('/'), MULTIPLY('\\*'), POWER('\\*\\*'), MODULO('%'), // binary operator
  ASSIGNMENT('='),
  EQUAL('=='), NOT_EQUAL('!='), STRICT_SUP('>'), STRICT_INF('<'), SUP('>='), INF('<='), AND('and'), OR('or'), //binary operator
  PARENT_OPEN('\\('), PARENT_CLOSE('\\)'), COMMA(','), SEMICOLON(';'), ACCOLADE_OPEN('\\{'), ACCOLADE_CLOSE('\\}'), BRACKET_OPEN('\\['), BRACKET_CLOSE('\\]'),

  IF('if'), ELSE('else'), FOR('for'), WHILE('while'), PRINT('print'), RETURN('return'),
  VAR('var'),

  STRING('\"(.*?)\"', {String v -> v.substring(1, v.length() - 1) }),
  CHAR("'(.*?)'", {String v -> v.charAt(1)}),
  INT('-?[0-9]+', Integer.&parseInt),
  FLOAT('-?([0-9]+\\.[0-9]*)|([0-9]*\\.[0-9]+)', Float.&parseFloat), TRUE('true', { -> true }), FALSE('false', { -> false }),

  TYPE_STRING('string'), TYPE_CHAR('char'), TYPE_INT('int'), TYPE_FLOAT('float'), TYPE_VOID('void'),
  IDENTIFIER('[a-zA-Z0-9]+', { v -> v}),
  END_OF_FILE('EOF'),

  //removable tokens
  WHITESPACE('[ ]+'),
  LINE_BREAK('\\R'),
  COMMENT_LINE('//(.*)\n'),
  COMMENT_BLOCK('/\\*.*\n.*\\*\\/');

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

}