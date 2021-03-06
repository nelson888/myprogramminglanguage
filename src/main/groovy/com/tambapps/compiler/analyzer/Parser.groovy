package com.tambapps.compiler.analyzer

import com.tambapps.compiler.analyzer.token.Token
import com.tambapps.compiler.analyzer.token.TokenNode
import com.tambapps.compiler.analyzer.token.TokenNodeType
import com.tambapps.compiler.analyzer.token.TokenType
import com.tambapps.compiler.analyzer.token.TokenUtils
import com.tambapps.compiler.exception.ParsingException

import static com.tambapps.compiler.analyzer.token.TokenUtils.ASSOCIATIVITY_MAP
import static com.tambapps.compiler.analyzer.token.TokenUtils.BINARY_OPERATOR_MAP
import static com.tambapps.compiler.analyzer.token.TokenUtils.PRIORITY_MAP
import static com.tambapps.compiler.analyzer.token.TokenUtils.VAR_TYPE_MAP

class Parser { //Syntax analyzer

  private def tokens //can be a list or an array of tokens
  private int currentIndex = 0

  /**
   *
   * @param tokens list or array of tokens
   * @return the Token tree
   */
  TokenNode parse(tokens) {
    this.tokens = tokens
    return program()
  }

  TokenNode parseFunc(tokens) {
    this.tokens = tokens
    return function()
  }

  //for console
  TokenNode parseInstructions(tokens) {
    this.tokens = tokens
    currentIndex = 0
    return instructions()
  }

  private TokenNode atom() {
    Token t = getCurrent()
    moveForward()
    switch (t.type) {
      case TokenType.STRING:
      case TokenType.CHAR:
      case TokenType.FLOAT:
      case TokenType.INT:
      case TokenType.TRUE:
      case TokenType.FALSE:
        return new TokenNode(t)
      case TokenType.IDENTIFIER:
        if (getCurrent().type == TokenType.PARENT_OPEN) {
          accept(TokenType.PARENT_OPEN)
          TokenNode N = new TokenNode(t, TokenNodeType.FUNCTION_CALL, [name: t.value])
          while (getCurrent().type != TokenType.PARENT_CLOSE) {
            N.addChild(expression()) //arg of function
            if (getCurrent().type == TokenType.PARENT_CLOSE) {
              break
            }
            accept(TokenType.COMMA)
          }
          accept(TokenType.PARENT_CLOSE)
          return N
        } else if (getCurrent().type == TokenType.BRACKET_OPEN) { //tab[n]
          accept(TokenType.BRACKET_OPEN)
          TokenNode N = new TokenNode(t, TokenNodeType.TAB_REF).withChildren(expression())
          accept(TokenType.BRACKET_CLOSE)
          return N
        } else if (getCurrent().type in [TokenType.INCREMENT, TokenType.DECREMENT]) {
          TokenNodeType type = getCurrent().type == TokenType.INCREMENT ?
              TokenNodeType.INCREMENT_AFTER : TokenNodeType.DECREMENT_AFTER
          moveForward()
          TokenNode identNode = new TokenNode(t, TokenNodeType.VAR_REF, [name: t.value])
          return new TokenNode(t, type, null).withChildren(identNode)
        }
        return new TokenNode(t, TokenNodeType.VAR_REF, [name: t.value])
      case TokenType.PLUS:
      case TokenType.MINUS:
      case TokenType.NOT:
      case TokenType.MULTIPLY: //gerer déreferencement
        TokenNode node = atom()
        return new TokenNode(TokenUtils.UNARY_OPERATOR_MAP.get(t.type), t, [node])
      case TokenType.PARENT_OPEN:
        TokenNode node = expression()
        if (getCurrent().type != TokenType.PARENT_CLOSE) {
          throw new ParsingException("Parenthesis should be close", node.l, node.c)
        }
        moveForward()
        return node
      case TokenType.INCREMENT:
      case TokenType.DECREMENT:
        TokenNodeType type = t.type == TokenType.INCREMENT ?
            TokenNodeType.INCREMENT_BEFORE : TokenNodeType.DECREMENT_BEFORE
        Token identTok = accept(TokenType.IDENTIFIER)
        TokenNode identNode = new TokenNode(identTok, TokenNodeType.VAR_REF, [name: identTok.value])
        return new TokenNode(t, type, null).withChildren(identNode)
      case TokenType.BRACKET_OPEN: //array
        TokenNode arrNode = new TokenNode(t, TokenNodeType.ARRAY)
        while (getCurrent().type != TokenType.BRACKET_CLOSE) {
          arrNode.addChild(atom())
          if (getCurrent().type == TokenType.COMMA) {
            accept(TokenType.COMMA)
          }
        }
        accept(TokenType.BRACKET_CLOSE)
        return arrNode
    }
    throw new ParsingException("Unexpected token $t.type encountered", t.l, t.c)
  }

  private TokenNode expression() {
    TokenNode expr = expression(Integer.MAX_VALUE)
    if (getCurrent().type == TokenType.QUESTION_MARK) {
      //terNode children: 1)evaluation 2)true value 3)false value
      TokenNode terNode = new TokenNode(accept(TokenType.QUESTION_MARK))
      terNode.addChildren(expr, expression())
      accept(TokenType.COLON)
      terNode.addChild(expression())
      return terNode
    }
    return expr
  }

  private TokenNode expression(int maxP) {
    TokenNode A = atom()
    Token T = getCurrent()
    while (T.type.isBinaryOperator() && PRIORITY_MAP.get(T.type) < maxP) {
      moveForward()
      TokenNode N = new TokenNode(T, BINARY_OPERATOR_MAP.get(T.type))
      N.addChild(A)
      N.addChild(expression(PRIORITY_MAP.get(T.type) + ASSOCIATIVITY_MAP.get(T.type)))
      A = N
      T = getCurrent()
    }
    return A
  }

  private TokenNode statement() {
    return statement(true)
  }

  private TokenNode statement(boolean semiColonRequired) {
    Token t = getCurrent()
    switch (t.type) {
      case TokenType.CONST: //constDecl child1: value
        moveForward()
        Token varTypeTok = getCurrent()
        if (!varTypeTok.type.varType) {
          throw new ParsingException("Expected a variable type but got $varTypeTok.type",
              varTypeTok.l, varTypeTok.c)
        }
        moveForward()
        Token tokIdent = accept(TokenType.IDENTIFIER)
        def declValue = [name: tokIdent.value, type: VAR_TYPE_MAP.get(varTypeTok.type)]
        accept(TokenType.ASSIGNMENT)
        TokenNode n = new TokenNode(tokIdent, TokenNodeType.CONST_DECL, declValue)
            .withChildren(expression())
        accept(TokenType.SEMICOLON, semiColonRequired)
        return n
      case TokenType.TYPE_STRING:
      case TokenType.TYPE_CHAR:
      case TokenType.TYPE_INT:
      case TokenType.TYPE_BOOL:
      case TokenType.FLOAT:
      case TokenType.VAR:
        TokenNode varDeclNode
        moveForward()
        Token tokIdent = accept(TokenType.IDENTIFIER)
        def declValue = [name: tokIdent.value, type: VAR_TYPE_MAP.get(t.type)]
        varDeclNode = new TokenNode(tokIdent, TokenNodeType.VAR_DECL, declValue)
        if (getCurrent().type == TokenType.SEMICOLON) {
          accept(TokenType.SEMICOLON)
          return varDeclNode
        } else if (getCurrent().type == TokenType.ASSIGNMENT) { //var ident = expr;
          Token assignToken = accept(TokenType.ASSIGNMENT)
          TokenNode seq = new TokenNode(assignToken, TokenNodeType.SEQ)
          TokenNode declTok = new TokenNode(tokIdent, TokenNodeType.VAR_DECL, declValue)
          TokenNode value = expression()
          TokenNode assignTok = new TokenNode(assignToken, TokenNodeType.ASSIGNMENT, null)
          assignTok.addChildren(new TokenNode(tokIdent, TokenNodeType.VAR_REF, [name: tokIdent.value]), value)
          seq.addChildren(declTok, assignTok)
          accept(TokenType.SEMICOLON, semiColonRequired)
          return seq
        } else if (getCurrent().type == TokenType.BRACKET_OPEN) { //var tab[n];
          TokenNode tabDeclNode = new TokenNode(tokIdent, TokenNodeType.TAB_DECL, declValue)
          accept(TokenType.BRACKET_OPEN)
          if (getCurrent().type != TokenType.BRACKET_CLOSE) {
            tabDeclNode.addChild(expression()) //index
          }
          accept(TokenType.BRACKET_CLOSE)
          accept(TokenType.SEMICOLON, semiColonRequired)
          return tabDeclNode
        }
        if (semiColonRequired) {
          throw new ParsingException("Expected token $TokenType.SEMICOLON or $TokenType.ASSIGNMENT", tokIdent.l, tokIdent.c)
        }
        return varDeclNode
      case TokenType.IF: // if (test) S
        TokenNode N = new TokenNode(accept(TokenType.IF))
        accept(TokenType.PARENT_OPEN)
        TokenNode test = expression()
        accept(TokenType.PARENT_CLOSE)
        TokenNode S = statement()
        N.addChild(test)
        N.addChild(S)
        if (getCurrent().type == TokenType.ELSE) {
          accept(TokenType.ELSE)
          N.addChild(statement())
        }
        return N
      case TokenType.SWITCH: //switch node child1: value children2..n: caseNodes
        TokenNode N = new TokenNode(accept(TokenType.SWITCH))
        accept(TokenType.PARENT_OPEN)
        N.addChild(expression())
        accept(TokenType.PARENT_CLOSE)
        accept(TokenType.ACCOLADE_OPEN)
        List<TokenNode> caseNodes = []
        while (getCurrent().type != TokenType.ACCOLADE_CLOSE) {
          if (getCurrent().type == TokenType.CASE) { //caseNode child1: value child2: statement seq EXCEPT default case
            Token ct = accept(TokenType.CASE)
            caseNodes.add(new TokenNode(ct)
                .withChildren(expression(), new TokenNode(ct, TokenNodeType.SEQ)))
            accept(TokenType.COLON)
          } else if (getCurrent().type == TokenType.DEFAULT) { //defaultCaseNode child1: statement seq
            Token ct = accept(TokenType.DEFAULT)
            caseNodes.add(new TokenNode(ct)
                .withChildren(new TokenNode(ct, TokenNodeType.SEQ)))
            accept(TokenType.COLON)
          } else {
            if (caseNodes.isEmpty()) {
              throw new ParsingException("Expected token CASE", getCurrent().l, getCurrent().c)
            }
            caseNodes*.lastChild //get the seq child of the case Node
                *.addChild(statement())
          }
        }
        accept(TokenType.ACCOLADE_CLOSE)
        caseNodes.each { N.addChild(it) }
        return N
      case TokenType.ACCOLADE_OPEN:
        TokenNode N = new TokenNode(accept(TokenType.ACCOLADE_OPEN))
        while (getCurrent().type != TokenType.ACCOLADE_CLOSE) {
          N.addChild(statement())
        }
        accept(TokenType.ACCOLADE_CLOSE)
        return N
      case TokenType.WHILE: //while (E) S
        TokenNode N = new TokenNode(accept(TokenType.WHILE)) //noeud loop
        TokenNode cond = new TokenNode(TokenNodeType.COND, N.l, N.c)
        N.addChild(cond)
        accept(TokenType.PARENT_OPEN)
        TokenNode test = expression()
        accept(TokenType.PARENT_CLOSE)
        TokenNode S = statement()
        cond.addChildren(test, S, new TokenNode(TokenNodeType.BREAK, cond.l, cond.c))
        return N
      case TokenType.FOR: //for (init;test;step) S
        TokenNode N = new TokenNode(accept(TokenType.FOR)) //noeud seq
        TokenNode loop = new TokenNode(TokenNodeType.LOOP, N.l, N.c)
        accept(TokenType.PARENT_OPEN)
        TokenNode init = statement()
        TokenNode test = expression()
        accept(TokenType.SEMICOLON)
        TokenNode step = expression()
        accept(TokenType.PARENT_CLOSE)
        TokenNode body = statement()

        TokenNode cond = new TokenNode(TokenNodeType.COND, loop.l, loop.c)
        TokenNode seq = new TokenNode(TokenNodeType.SEQ, step.l, step.c)
        TokenNode breakNode = new TokenNode(TokenNodeType.BREAK, step.l, step.c)

        N.addChildren(init, loop)
        loop.addChild(cond)
        cond.addChildren(test, seq, breakNode)
        seq.addChildren(body, step)
        return new TokenNode(t, TokenNodeType.BLOC).withChildren(N)
      case TokenType.PRINT:
        TokenNode print = new TokenNode(accept(TokenType.PRINT))
        TokenNode e = expression()
        print.addChild(e)
        accept(TokenType.SEMICOLON, semiColonRequired)
        return print

      case TokenType.RETURN:
        TokenNode n = new TokenNode(accept(TokenType.RETURN))
        n.addChild(expression())
        accept(TokenType.SEMICOLON, semiColonRequired)
        return n
      case TokenType.BREAK:
      case TokenType.CONTINUE:
        moveForward()
        accept(TokenType.SEMICOLON, semiColonRequired)
        return new TokenNode(t)
      default: // expression;
        TokenNode e = expression()
        return new TokenNode(TokenNodeType.DROP, accept(TokenType.SEMICOLON, semiColonRequired), [e])
    }
  }

  private TokenNode program() {
    TokenNode p = new TokenNode(TokenNodeType.PROG, 0, 0)
    while (getCurrent().type != TokenType.END_OF_FILE) {
      p.addChild(function())
    }
    return p
  }

  private TokenNode instructions() {
    TokenNode p = new TokenNode(TokenNodeType.SEQ, 0, 0)
    while (getCurrent().type != TokenType.END_OF_FILE) {
      p.addChild(statement(false))
    }
    return p
  }

  private TokenNode function() {
    Token t = accept(TokenType.IDENTIFIER)
    TokenNode n = new TokenNode(t, TokenNodeType.FUNCTION, [name: t.value])
    accept(TokenType.PARENT_OPEN)
    while (getCurrent().type != TokenType.PARENT_CLOSE) {
      Token typeTok = getCurrent()
      moveForward()
      if (!typeTok.type.varType) {
        throw new ParsingException("Expected type for argument variable", typeTok.l, typeTok.c)
      }
      Token acc = accept(TokenType.IDENTIFIER)
      n.addChild(new TokenNode(acc, TokenNodeType.VAR_DECL, [name: acc.value, type: VAR_TYPE_MAP.get(typeTok.type)]))
      if (getCurrent().type == TokenType.COMMA) {
        accept(TokenType.COMMA)
      }
    }
    accept(TokenType.PARENT_CLOSE)
    n.addChild(statement())
    return n
  }

  private void moveForward() {
    currentIndex++
  }

  private Token getCurrent() {
    if (currentIndex >= tokens.size()) {
      Token last = tokens[-1]
      throw new ParsingException("Unexpected end of file", last.l, last.c)
    }
    return tokens[currentIndex]
  }

  void reset() {
    tokens = null
    currentIndex = 0
  }

  Token accept(TokenType t) {
    Token token = getCurrent()
    if (token.type != t) {
      throw new ParsingException("Expected token of type $t but got $token.type", token.l, token.c)
    }
    moveForward()
    return token
  }

  Token accept(TokenType t, boolean required) {
    Token token = getCurrent()
    if (token.type == t) {
      moveForward()
    } else if (required) {
      throw new ParsingException("Expected token of type $t but got $token.type", token.l, token.c)
    }
    return new Token(token.l, token.c, token.value, t)
  }
}
