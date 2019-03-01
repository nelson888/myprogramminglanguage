package com.tambapps.compiler.eval.console

import static com.tambapps.compiler.eval.Evaluator.VOID

import com.tambapps.compiler.analyzer.LexicalAnalyzer
import com.tambapps.compiler.analyzer.Parser
import com.tambapps.compiler.analyzer.token.Token
import com.tambapps.compiler.analyzer.token.TokenNode
import com.tambapps.compiler.exception.EvaluationException
import com.tambapps.compiler.exception.LexicalException
import com.tambapps.compiler.exception.ParsingException

class Console {

  private static final String FUNC_DEF_KEYWORD = 'def'
  private static final String PROMPT = '> '

  private final List<TokenNode> functions = []
  private final List<CFunction> cFunctions = []
  private final LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer()
  private final Parser parser = new Parser()
  private final CEvaluator evaluator = new CEvaluator(functions, cFunctions, System.out.&println)
  private boolean running

  Console() {
    cFunctions.addAll(CFunctions.getAll(evaluator))
    cFunctions.add(new CFunction("exit", [], {running = false; VOID }))
  }

  void prompt() {
    Scanner scanner = new Scanner(System.in)
    String code = ''
    println('###    Welcome to the BOB language terminal                                ###')
    println("###    To define a function, start with the 'def' keywords                 ###")
    println("###    If you want to write many instructions on a row, write them on a bloc ({ })   ###")
    println()
    print(PROMPT)
    running = true
    while (running) {
      code += ' ' + scanner.nextLine().trim()
      code = code.trim()
      int occOp = nbOcc(code, '{')
      if (occOp > 0 && occOp != nbOcc(code, '}')) {
        continue
      }
      if (code.startsWith(FUNC_DEF_KEYWORD)) {
        process(code.substring(FUNC_DEF_KEYWORD.length()), true)
        code = ''
      } else {
        process(code, false)
        code = ''
      }
      if (running) {
        print(PROMPT)
      }
    }
    println("Exited terminal")
  }

  private void process(String text, boolean funcDef) {
    try {
      List<Token> tokens = lexicalAnalyzer.toTokens(text)
      TokenNode node
      if (funcDef) {
        node = parser.parseFunc(tokens)
        if (funcAlreadyExists(node.value.name)) {
          throw new EvaluationException("Function already exists", node.l, node.c)
        }
        functions.add(node)
      } else {
        node = parser.parseInstructions(tokens)
        def value = evaluator.process(node)
        if  (value != VOID) {
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
