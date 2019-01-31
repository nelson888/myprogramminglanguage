package com.tambapps.compiler.analyzer

import com.tambapps.compiler.analyzer.token.Token
import com.tambapps.compiler.analyzer.token.TokenType
import com.tambapps.compiler.exception.LexicalException

import java.util.regex.Matcher
import java.util.regex.Pattern

//thread safe
class LexicalAnalyzer {

  private static final Pattern TOKEN_PATTERN
  private static final String GROUP = 'G'

  static {
    StringBuilder builder = new StringBuilder()

    TokenType[] types = TokenType.values()
    for (int i = 0; i < types.length; i++) {
      builder.append("|(?<${GROUP + i}>${types[i].value})")
    }
    TOKEN_PATTERN = Pattern.compile(builder.substring(1))
  }

  List<Token> toTokens(File file) throws LexicalException {
    return toTokens(file.getText())
  }

  List<Token> toTokens(String content) throws LexicalException {
    return convert(content)
  }

  private List<Token> convert(String content) throws LexicalException {
    List<Token> tokens = new ArrayList<>()
    TokenType[] types = TokenType.values()
    int l = 0
    int c = 0
    // Begin matching tokens
    Matcher matcher = TOKEN_PATTERN.matcher(content)
    while (matcher.find()) {
      for (int i = 0; i < types.length; i++) {
        TokenType t = types[i]
        String match = matcher.group(GROUP + i) //group 0 is entire pattern
        if (match != null) {
          if (!t.removable) {
            if (t == TokenType.CHAR && match.size() != 3) {
              throw new LexicalException("$match is not a character", l ,c)
            }
            tokens.add(new Token(l, c, t.matchMap(match), t))
          }
          if (t == TokenType.LINE_BREAK) {
            l++
            c = 0
          } else {
            c += match.length()
          }
          break
        }
      }
    }
    tokens.add(new Token(l + 1, c, TokenType.END_OF_FILE))
    return tokens
  }

  void reset() {}
}
