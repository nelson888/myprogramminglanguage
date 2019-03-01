package com.tambapps.compiler.eval.console

import com.tambapps.compiler.analyzer.LexicalAnalyzer
import com.tambapps.compiler.analyzer.Parser
import com.tambapps.compiler.analyzer.token.Token
import com.tambapps.compiler.analyzer.token.TokenNode
import com.tambapps.compiler.eval.Evaluator
import com.tambapps.compiler.exception.EvaluationException
import com.tambapps.compiler.exception.LexicalException
import com.tambapps.compiler.exception.ParsingException

//TODO don't return ARRAY on returnValue
class Console {

  private static final String FUNC_DEF_KEYWORD = 'def'
  private static final String PROMPT = '> '
  private final List<TokenNode> functions = []
  private final List<CFunction> cFunctions = []
  private final LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer()
  private final Parser parser = new Parser()
  private final CEvaluator evaluator = new CEvaluator(functions, cFunctions, System.out.&println)
  private boolean running

  void prompt() {
    Scanner scanner = new Scanner(System.in)
    String code = ''
    println('###    Welcome to the BOB language terminal                                ###')
    println("###    To define a function, start with the 'def' keywords                 ###")
    println("###    You can write instructions, put ';' at the end to interpret them    ###")
    println()
    print(PROMPT)
    running = true
    while (running) {
      code += ' ' + scanner.nextLine().trim()
      code = code.trim()
      int occOp = nbOcc(code, '{')
      if (occOp > 0 && occOp == nbOcc(code, '}')) {
        continue
      }
      if (code.startsWith(FUNC_DEF_KEYWORD)) {
        process(code.substring(FUNC_DEF_KEYWORD.length()), true)
        code = ''
      } else {
        process(code, false)
        code = ''
      }
      print(PROMPT)
    }
  }

  private void process(String text, boolean funcDef) {
    try {
      List<Token> tokens = lexicalAnalyzer.toTokens(text)
      TokenNode node
      if (funcDef) {
        node = parser.parseFunc(tokens)
        if (funcAlreadyExists(node.value.name)) {
          throw new EvaluationException("Function already exists", node.c, node.l)
        }
        functions.add(node)
      } else {
        node = parser.parseInstructions(tokens)
        def value = evaluator.process(node)
        if  (value != Evaluator.VOID) {
          println(value)
        }
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

  private static int nbOcc(String text, String c) {
    return text.findAll({ it == c }).size()
  }

  private boolean funcAlreadyExists(String name) {
    return functions.find {name == it.value.name } || cFunctions.find {name == it.name }
  }

  public static void main(String[] args) {
    new Console().prompt()
  }
}
