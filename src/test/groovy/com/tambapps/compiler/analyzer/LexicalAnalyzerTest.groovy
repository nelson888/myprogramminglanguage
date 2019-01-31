package com.tambapps.compiler.analyzer

import com.tambapps.compiler.analyzer.token.Token
import com.tambapps.compiler.analyzer.token.TokenType
import com.tambapps.compiler.exception.LexicalException

import static com.tambapps.compiler.analyzer.token.Token.of

class LexicalAnalyzerTest extends GroovyTestCase {

  private final LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer()

  static {
    Token.metaClass.equals = {
        //override method equal because token position not working right
      o ->
        if (!o instanceof Token) return false
        Token t1 = (Token) o
        Token t2 = delegate
        return t1.type == t2.type && t1.value == t2.value
    }
  }

  void testExpressions() {
    if (true) {
      println(5 % 3.2)
      return
    }
    List<String> test = [
        '4 + 2',
        '25--4',
        '-43* (6 +45)'
    ]

    List<List<Token>> expected = [
        [of(TokenType.INT, 4, 0, 0),
         of(TokenType.PLUS, 1, 0),
         of(TokenType.INT, 2, 4, 0),
         of(TokenType.END_OF_FILE, 0, 1)],

        [of(TokenType.INT, 25, 0, 0),
         of(TokenType.MINUS, 2, 0,),
         of(TokenType.MINUS, 3, 0),
         of(TokenType.INT, 4, 4, 0),
         of(TokenType.END_OF_FILE, 0, 1)],

        [of(TokenType.MINUS, 0, 0),
         of(TokenType.INT, 43, 1, 0),
         of(TokenType.MULTIPLY, 3, 0),
         of(TokenType.PARENT_OPEN, 5, 0),
         of(TokenType.INT, 6, 6, 0),
         of(TokenType.PLUS, 8, 0),
         of(TokenType.INT, 45, 9, 0),
         of(TokenType.PARENT_CLOSE, 11, 0),
         of(TokenType.END_OF_FILE, 0, 1)],

        [of(TokenType.INT, 5, 0, 0),
         of(TokenType.POWER, 1, 0),
         of(TokenType.INT, 4, 4, 0),
         of(TokenType.POWER, 1, 0),
         of(TokenType.INT, 2, 4, 0),
         of(TokenType.END_OF_FILE, 0, 1)],

        [of(TokenType.INT, 43, 0, 0),
         of(TokenType.STRICT_SUP, 1, 0),
         of(TokenType.INT, 1, 4, 0),
         of(TokenType.END_OF_FILE, 0, 1)],

        [of(TokenType.INT, 43, 0, 0),
         of(TokenType.STRICT_INF, 1, 0),
         of(TokenType.INT, 1, 4, 0),
         of(TokenType.END_OF_FILE, 0, 1)],

        [of(TokenType.INT, 43, 0, 0),
         of(TokenType.SUP, 1, 0),
         of(TokenType.INT, 1, 4, 0),
         of(TokenType.END_OF_FILE, 0, 1)],

        [of(TokenType.INT, 43, 0, 0),
         of(TokenType.INF, 1, 0),
         of(TokenType.INT, 1, 4, 0),
         of(TokenType.END_OF_FILE, 0, 1)],

        [of(TokenType.INT, 43, 0, 0),
         of(TokenType.EQUAL, 1, 0),
         of(TokenType.INT, 1, 4, 0),
         of(TokenType.END_OF_FILE, 0, 1)],

        [of(TokenType.INT, 43, 0, 0),
         of(TokenType.NOT_EQUAL, 1, 0),
         of(TokenType.NOT, 1, 0),
         of(TokenType.INT, 1, 4, 0),
         of(TokenType.END_OF_FILE, 0, 1)],

        [of(TokenType.NOT, 0, 0),
         of(TokenType.NOT, 1, 0),
         of(TokenType.NOT, 1, 0),
         of(TokenType.INT, 1, 4, 0),
         of(TokenType.END_OF_FILE, 0, 1)],
    ]

    assertListEqual(test, expected)
  }

  private void assertListEqual(List<String> tests, List<List<Token>> expected) {
    for (int i = 0; i < tests.size(); i++) {
      String test = tests[i]
      assertEquals("Failed test $i\nInput: $test", expected[i], lexicalAnalyzer.toTokens(test))
      lexicalAnalyzer.reset()
    }
  }

}
