package com.tambapps.compiler.eval

import com.tambapps.compiler.analyzer.LexicalAnalyzer
import com.tambapps.compiler.analyzer.Parser
import com.tambapps.compiler.analyzer.token.Token
import com.tambapps.compiler.analyzer.token.TokenNode
import com.tambapps.compiler.exception.EvaluationException
import com.tambapps.compiler.exception.LexicalException
import com.tambapps.compiler.exception.ParsingException

/* TODO implement ConsoleEvaluator extends Evaluator with function with special behavior. e.g:
 exit() => exit(0)
 exit(int) => exit with the argument value

*/
class Console {

  private static final String FUNC_DEF_KEYWORD = 'def'
  private final List<TokenNode> functions = []
  private final LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer()
  private final Parser parser = new Parser()
  private final Evaluator evaluator = new Evaluator(functions)

  void scan() {
    Scanner scanner = new Scanner(System.in)
    String code = ''
    println('Welcome to the myprogramminglanguage terminal')
    println("To define a function, start with the 'def' keywords")
    println("You can write instructions, put ';' at the end to interpret them")
    println()
    while (true) {
      code += ' ' + scanner.nextLine().trim()
      code = code.trim()
      if (code.startsWith(FUNC_DEF_KEYWORD)) {
        int occOp = nbOcc(code, '{')
        if (occOp > 0 && occOp == nbOcc(code, '}')) {
          process(code.substring(FUNC_DEF_KEYWORD.length()), true)
          code = ''
        }
      } else {
        if (code.trim().endsWith(';')) {
          process(code, false)
          code = ''
        }
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
      } else { //TODO bug
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

  private static int nbOcc(String text, String c) {
    return text.findAll({ it == c }).size()
  }

  public static void main(String[] args) {
    new Console().scan()
  }
}
