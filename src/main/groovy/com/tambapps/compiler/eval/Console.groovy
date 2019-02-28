package com.tambapps.compiler.eval

import com.tambapps.compiler.analyzer.LexicalAnalyzer
import com.tambapps.compiler.analyzer.Parser
import com.tambapps.compiler.analyzer.token.Token
import com.tambapps.compiler.analyzer.token.TokenNode
import com.tambapps.compiler.exception.EvaluationException
import com.tambapps.compiler.exception.LexicalException
import com.tambapps.compiler.exception.ParsingException

class Console {

  private static final String FUNC_DEF_KEYWORD = 'def'
  private final List<TokenNode> functions = []
  private final LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer()
  private final Parser parser = new Parser()
  private final Evaluator evaluator = new Evaluator(functions)

  void scan() {
    Scanner scanner = new Scanner(System.in)
    String code = ''
    while (true) {
      code += ' ' + scanner.nextLine().trim()
      if (code.trim().endsWith(';')) { //TODO find a way to not having to put ';' for func def
        boolean isFuncDef = code.startsWith(FUNC_DEF_KEYWORD)
        process(isFuncDef ? code.substring(FUNC_DEF_KEYWORD.length()) : code, isFuncDef)
      }
    }
  }

  private void process(String text, boolean funcDef) {
    try {
      List<Token> tokens = lexicalAnalyzer.toTokens(text)
      TokenNode node
      if (funcDef) {
        node = parser.parseFunc(tokens)
        functions.add(node)
      } else {
        node = parser.parseInstructions(tokens)
        evaluator.process(node)
      }
    } catch (LexicalException e) {
      printlnErr('Error while performing lexical analysis')
      printlnErr("$e.message")
      lexicalAnalyzer.reset()
    } catch (ParsingException e) {
      printlnErr('Error while performing parsing')
      printlnErr("$e.message")
      parser.reset()
    }  catch (EvaluationException e) {
      printlnErr('Error while evaluating instructions')
      printlnErr("$e.message")
    }
  }

  private static void printlnErr(String text) {
    System.err.println(text)
  }

  public static void main(String[] args) {
    new Console().scan()
  }
}
