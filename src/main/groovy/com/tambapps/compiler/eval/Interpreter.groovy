package com.tambapps.compiler.eval

import com.tambapps.compiler.analyzer.LexicalAnalyzer
import com.tambapps.compiler.analyzer.Parser

import com.tambapps.compiler.analyzer.token.Token
import com.tambapps.compiler.analyzer.token.TokenNode
import com.tambapps.compiler.exception.EvaluationException
import com.tambapps.compiler.exception.LexicalException
import com.tambapps.compiler.exception.ParsingException
import com.tambapps.compiler.exception.PointerException

class Interpreter {

  private final LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer()
  private final Parser parser = new Parser()

  void interpret(String text, final Closure println) {

    lexicalAnalyzer.reset()
    parser.reset()

    TokenNode program
    try {
      List<Token> tokens = lexicalAnalyzer.toTokens(text)
      program = parser.parse(tokens)
      print(program.treeString())
    } catch (LexicalException e) {
      println('Error while performing lexical analysis')
      println("$e.message")
      return
    } catch (ParsingException e) {
      println('Error while performing parsing')
      println("$e.message")
      return
    } catch (EvaluationException e) {
      println('Error while performing semantic analysis')
      println("$e.message")
      return
    }

    List<TokenNode> functions = new ArrayList<>()
    TokenNode main = null
    for (int i = 0; i < program.nbChildren(); i++) {
      TokenNode function = program.getChild(i)
      if (function.value.name == "main") {
        main = function
      } else {
        functions.add(function)
      }
    }
    if (!main) {
      println("Doesn't have a main function!!!")
      return
    }
    Evaluator evaluator = new Evaluator(functions, println)
    try {
      evaluator.process(main)
    } catch (PointerException e) {
      println("Pointer exception:\n$e.message")
    }
    def returnValue = evaluator.returnValue
    if (returnValue != null) {
      println("Exited with value $returnValue")
    }
  }
}

