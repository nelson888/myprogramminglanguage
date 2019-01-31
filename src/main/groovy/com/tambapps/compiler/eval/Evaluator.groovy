package com.tambapps.compiler.eval

import com.tambapps.compiler.exception.NoSuchOperatorException

import com.tambapps.compiler.exception.WrongTypeException

import static com.tambapps.compiler.analyzer.token.TokenUtils.OPERATOR_MAP

import com.tambapps.compiler.analyzer.token.TokenNode
import com.tambapps.compiler.analyzer.token.TokenNodeType
import com.tambapps.compiler.exception.PointerException
import com.tambapps.compiler.util.DequeMap
import com.tambapps.compiler.util.Symbol
import com.tambapps.compiler.util.Symbol.Type

class Evaluator {

  private final List<TokenNode> functions
  private final Closure printer
  private DequeMap dequeMap
  private def returnValue = null
  private int nbSlot = 0


  Evaluator(List<TokenNode> functions, Closure printer) {
    this.functions = functions
    this.printer = printer
    dequeMap = new DequeMap()
  }

  Evaluator(List<TokenNode> functions) {
    this(functions, System.out.&println)
  }

  void process(TokenNode node) throws PointerException {
    switch (node.type) {
      case TokenNodeType.VAR_DECL:
        Symbol s = dequeMap.newSymbol(node.value.name)
        s.slot = nbSlot++
        s.type = node.value.type
        break
      case TokenNodeType.TAB_DECL: //child => size
        String tabName = node.value.name
        def defaultValue = node.value.type.defaultValue
        Symbol s = dequeMap.newSymbol(tabName)
        s.slot = nbSlot++
        s.type = node.value.type
        int size = evaluate(node.getChild(0), Type.INT)
        for (int i = 0; i < size; i++) {
          s = dequeMap.newSymbol(i + tabName)
          s.slot = nbSlot++
          s.value = defaultValue
          s.type = node.value.type
        }
        break
      case TokenNodeType.BLOC:
        dequeMap.newBlock()
        for (int i = 0; i < node.nbChildren(); i++) {
          process(node.getChild(i))
        }
        dequeMap.endBlock()
        break
      case TokenNodeType.ASSIGNMENT:
        TokenNode left = node.getChild(0)
        Symbol s
        def value = evaluate(node.getChild(1))
        if (left.type == TokenNodeType.D_REF) {
          s = dequeMap.findSymbol(left.getChild(0).value)
          if (s.slot > nbSlot) {
            throw new PointerException("Pointed variable with address $s.value doesn't exist", node)
          }
          s = dequeMap.findSymbolWithSlot(s.value)
        } else if (left.type == TokenNodeType.TAB_REF) {
          int index = evaluate(left.getChild(0)) + 1 //because 0 is the tab variable itself
          s = dequeMap.findSymbol(left.value)
          if (s.slot + index > nbSlot) {
            throw new PointerException("Tried to access element $index of array $left.value", node)
          }
          s = dequeMap.findSymbolWithSlot(s.slot + index)
        } else {
          s = dequeMap.findSymbol(left.value)
        }
        if (!s.type.isType(value)) {
          throw new WrongTypeException(s.type, value, node)
        }
        s.value = value
        break
      case TokenNodeType.COND:
        TokenNode condition = node.getChild(0)
        if (evaluate(condition)) {
          process(node.getChild(1))
        } else if (node.nbChildren() > 2) {
          process(node.getChild(2))
        }
        break
      case TokenNodeType.LOOP:
        TokenNode condNode = node.getChild(0)
        TokenNode testNode = condNode.getChild(0)
        while (evaluate(testNode)) {
          process(condNode.getChild(1))
        }
        break
      case TokenNodeType.RETURN:
        if (node.nbChildren() > 0) {
          TokenNode returnExpression = node.getChild(0)
          returnValue = evaluate(returnExpression)
        }
        break
      case TokenNodeType.FUNCTION_CALL: //like a procedure call
        evaluate(node)
        break
      case TokenNodeType.PRINT:
        printer(evaluate(node.getChild(0)))
        break
      case TokenNodeType.SEQ:
        for (int i = 0; i < node.nbChildren(); i++) {
          process(node.getChild(i))
        }
        break
      default:
        for (int i = 0; i < node.nbChildren(); i++) {
          process(node.getChild(i))
        }
    }
  }

  private def evaluate(TokenNode n, Type type) {
    def value = evaluate(n)
    if (!type.isType(value)) {
      throw new WrongTypeException(type, value, n)
    }
    return value
  }

  private def evaluate(TokenNode e) { //evaluates an expression
    if (e.type.unaryOperator) {
      def arg = evaluate(e.getChild(0))
      Type type = Type.toType(arg)
      if (!e.type.canOperate(type)) {
        throw new NoSuchOperatorException(e.type, type, e)
      }
      return OPERATOR_MAP.get(e.type).call(arg)
    } else if (e.type.binaryOperator) {
      def arg1 = evaluate(e.getChild(0))
      def arg2 = evaluate(e.getChild(1))
      List<Type> types = [arg1, arg2].collect { v -> Type.toType(v)}
      if (!e.type.canOperate(*types)) {
        throw new NoSuchOperatorException(e.type, *types, e)
      }
      return OPERATOR_MAP.get(e.type).call(arg1, arg2)
    } else if (e.type.value) {
      return e.value
    }
    switch (e.type) {
      case TokenNodeType.VAR_REF:
        return dequeMap.findSymbol(e.value).value
      case TokenNodeType.D_REF:
        return dequeMap.findSymbol(e.getChild(0).value).slot
      case TokenNodeType.TAB_REF:
        int index = evaluate(e.getChild(0)) + 1 //because 0 is the tab variable itself
        Symbol s = dequeMap.findSymbol(e.value)
        if (s.slot + index > nbSlot) {
          throw new PointerException("Tried to access element $index of array $e.value", node)
        }
        Symbol pointedSymbol = dequeMap.findSymbolWithSlot(s.slot + index)
        return pointedSymbol.value

      /*
      case TokenNodeType.INCREMENT:
      case TokenNodeType.DECREMENT:
        int arg1 = evaluate(e.getChild(0));
        Symbol s = dequeMap.findSymbol(e.value)
        return s.value++;*/
      case TokenNodeType.FUNCTION_CALL:
        Symbol funcData = dequeMap.findSymbol(e.value)
        TokenNode function = functions.find({ f -> funcData == f.value })
        Evaluator evaluator = new Evaluator(functions, printer)
        List<Symbol> arguments = new ArrayList<>()
        for (int i = 0; i < funcData.nbArgs; i++) {
          Symbol argument = dequeMap.findSymbol(function.getChild(i).value).copy()
          argument.value = evaluate(e.getChild(i))
          arguments.add(argument)
        }
        evaluator.initVariables(arguments)
        evaluator.process(function.getChild(function.nbChildren() - 1)) //skip variable declarations
        return evaluator.getReturnValue()

      default:
        throw new RuntimeException("This shouldn't happen")
    }
  }

  private void initVariables(List<Symbol> symbols) { //for function call
    for (Symbol symbol : symbols) {
      dequeMap.newSymbol(symbol)
    }
  }

  Integer getReturnValue() {
    return returnValue
  }

  private void checkNumber(TokenNode n) {
    if (n.type.isUnaryOperator()) {
      checkNumber(n.getChild(0))
    } else if (n.type.isBinaryOperator()) {
      checkNumber(n.getChild(0))
      checkNumber(n.getChild(1))
    } else if (!isOfType(Type.INT, n) && !isOfType(Type.FLOAT, n)) {
      throw new WrongTypeException("Expected number", n.l, n.c)
    }
  }

  private boolean isOfType(Type type, TokenNode token) {
    return type.isNode(token.type) || isVarOfType(type, token)
  }

  private boolean isVarOfType(Type type, TokenNode token) {
    if (token.type == TokenNodeType.VAR_REF) {
      Symbol symbol = dequeMap.findSymbol(token.value.name)
      return symbol.type == type
    }
    return false
  }
}
