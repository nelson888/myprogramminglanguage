package com.tambapps.compiler.eval

import com.tambapps.compiler.exception.EvaluationException
import com.tambapps.compiler.exception.IllegalStatementException
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
  private final Queue<TokenNodeType> loopInterruptQueue = new ArrayDeque<>()
  private int loops = 0 //number of nested loops
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

  void process(TokenNode node) throws EvaluationException {
    switch (node.type) {
      case TokenNodeType.VAR_DECL:
        Symbol s = dequeMap.newSymbol(node.value.name)
        s.slot = nbSlot++
        s.type = node.value.type
        s.value = s.type.defaultValue
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
          if (!loopInterruptQueue.isEmpty()) {
            def type = loopInterruptQueue.first()
            if (loops == 0) {
              throw new IllegalStatementException("Can't use $type outside of a loop", node)
            }
            break
          }
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
          if (s.type == Type.STRING) {
            String str = s.value
            index--
            if (index >= str.size()) {
              throw new PointerException("Tried to access character $index of string \"$str\"", node)
            }
            Type t = Type.fromValue(value)
            if (t != Type.CHAR) {
              throw new WrongTypeException(Type.STRING, t, node)
            }
            StringBuilder builder = new StringBuilder()
            for (int i = 0; i < str.size(); i++) {
              if (i == index) {
                builder.append(value)
              } else {
                builder.append(str.charAt(i))
              }
            }
            s.value = builder.toString()
            break
          }
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
        loops++
        boolean brokeLoop = false
        while (!brokeLoop && evaluate(testNode)) {
          process(condNode.getChild(1))
          if (!loopInterruptQueue.empty) {
            def type = loopInterruptQueue.remove()
            if (type == TokenNodeType.BREAK) { //continue is already handled
              brokeLoop = true
            }
          }
        }
        loops--
        break
      case TokenNodeType.RETURN:
        if (node.nbChildren() > 0) {
          TokenNode returnExpression = node.getChild(0)
          returnValue = evaluate(returnExpression)
        }
        break
      case TokenNodeType.FUNCTION_CALL: //like a procedure call
        functionCall(node)
        break
      case TokenNodeType.TERNARY:
        evaluate(node)
        break
      case TokenNodeType.PRINT:
        printer(evaluate(node.getChild(0)))
        break
      case TokenNodeType.FUNCTION:
      case TokenNodeType.SEQ:
        for (int i = 0; i < node.nbChildren(); i++) {
          process(node.getChild(i))
        }
        break
      case TokenNodeType.CONTINUE:
      case TokenNodeType.BREAK:
        loopInterruptQueue.add(node.type)
        break
      case TokenNodeType.INCREMENT_BEFORE:
      case TokenNodeType.DECREMENT_BEFORE:
      case TokenNodeType.INCREMENT_AFTER:
      case TokenNodeType.DECREMENT_AFTER:
        evaluate(node)
        break
      case TokenNodeType.DROP:
        break
      default:
        throw new RuntimeException("Unhandled node type $node.type")
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
      Type type = Type.fromValue(arg)
      if (!e.type.canOperate(type)) {
        throw new NoSuchOperatorException(e.type, type, e)
      }
      return OPERATOR_MAP.get(e.type).call(arg)
    } else if (e.type.binaryOperator) {
      def arg1 = evaluate(e.getChild(0))
      def arg2 = evaluate(e.getChild(1))
      List<Type> types = [arg1, arg2].collect { v -> Type.fromValue(v)}
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
        if (s.type == Type.STRING) {
          String str = s.value
          if (index >= str.size()) {
            throw new PointerException("Tried to access character $index of string \"$str\"", e)
          }
          return str.charAt(index - 1)
        }
        if (s.slot + index > nbSlot) {
          throw new PointerException("Tried to access element $index of array $e.value", e)
        }
        Symbol pointedSymbol = dequeMap.findSymbolWithSlot(s.slot + index)
        return pointedSymbol.value
      case TokenNodeType.INCREMENT_BEFORE:
      case TokenNodeType.DECREMENT_BEFORE:
      case TokenNodeType.INCREMENT_AFTER:
      case TokenNodeType.DECREMENT_AFTER:
        def varNode = e.getChild(0)
        def arg1 = evaluate(varNode)
        Symbol s = dequeMap.findSymbol(varNode.value)
        Type argType = Type.fromValue(arg1)
        if (!e.type.canOperate(argType)) {
          throw new NoSuchOperatorException(e.type, argType, e)
        }
        def newVal =  OPERATOR_MAP.get(e.type).call(s.value)
        if (e.type in [TokenNodeType.INCREMENT_BEFORE, TokenNodeType.DECREMENT_BEFORE]) {
          s.value = newVal
          return newVal
        } else {
          def oldVal = s.value
          s.value = newVal
          return oldVal
        }
      case TokenNodeType.FUNCTION_CALL:
        def value = functionCall(e)
        if (value == null) {
          throw new WrongTypeException("Expected a value to be returned from function $e.value.name",
              e.l, e.c)
        }
        return value
      case TokenNodeType.TERNARY:
        def test = evaluate(e.getChild(0))
        return evaluate(test ? e.getChild(1) : e.getChild(2))
      default:
        throw new RuntimeException("This shouldn't happen")
    }
  }

  def functionCall(TokenNode e) {
    TokenNode function = functions.find({ f -> e.value.name == f.value.name })
    int nbArgs = function.nbChildren() - 1
    int nbChildren = e.nbChildren()
    if (nbChildren != nbArgs) {
      throw new EvaluationException('There is ' +
          (nbChildren < nbArgs ? 'not enough' : 'too much') +
          " arguments to call function $function.value.name (expected $nbArgs, found $nbChildren)",
          e.l, e.c)
    }
    Evaluator evaluator = new Evaluator(functions, printer)
    for (int i = 0; i < nbArgs; i++) {
      def argData = function.getChild(i).value
      Symbol argument = dequeMap.findSymbol(function.getChild(i).value).copy()
      def argValueNode = e.getChild(i)
      def argValue = evaluate(argValueNode)
      argument.type = argData.type
      if (!argData.type.isType(argValue)) {
        throw new WrongTypeException(argData.type, argValue, argValueNode)
      }
      argument.value = argValue
      evaluator.dequeMap.insertSymbol(argData.name, argument)
    }
    evaluator.dequeMap
    evaluator.process(function.getChild(function.nbChildren() - 1)) //skip variable declarations
    return evaluator.getReturnValue()
  }

  def getReturnValue() {
    return returnValue
  }
}
