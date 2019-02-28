package com.tambapps.compiler.eval

import com.tambapps.compiler.exception.EvaluationException
import com.tambapps.compiler.exception.IllegalStatementException
import com.tambapps.compiler.exception.NoSuchOperatorException

import com.tambapps.compiler.exception.WrongTypeException
import com.tambapps.compiler.util.Array

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
        s.type = Type.arrayType(node.value.type)
        s.value = new Array(node.value.type)
        if (node.nbChildren() > 0) {
          int size = evaluate(node.getChild(0), Type.INT)
          for (int i = 0; i < size; i++) {
            s.value.append(defaultValue)
          }
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
        if (left.type == TokenNodeType.TAB_REF) {
          int index = evaluate(left.getChild(0))
          s = dequeMap.findSymbol(left.value)
          def array = s.value
          if (array.size() <= index) {
            throw new PointerException("Tried to access element $index of array $left.value " +
                "with size ${array.size()}", node)
          }
          Type elementType = s.type.elementType()
          if (!elementType.isType(value)) {
            throw new WrongTypeException(elementType, value, node)
          }
          array[index] = value
          break
        }
        if (left.type == TokenNodeType.D_REF) {
          s = dequeMap.findSymbol(left.getChild(0).value)
          if (s.slot > nbSlot) {
            throw new PointerException("Pointed variable with address $s.value doesn't exist", node)
          }
          s = dequeMap.findSymbolWithSlot(s.value)
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
      case TokenNodeType.SWITCH:
        def value = evaluate(node.getChild(0))
        TokenNode caseNode = node.childrenIterator().find {
          it.type == TokenNodeType.CASE &&
              !isDefaultCaseNode(it) && evaluate(it.getChild(0)) == value
        }
        if (!caseNode) { // if no case, look for default case
          caseNode = node.childrenIterator().find(Evaluator.&isDefaultCaseNode)
        }
        if (!caseNode) {
          break
        }
        TokenNode statementsNode = caseNode.lastChild
        for (def statement : statementsNode.childrenIterator()) {
          process(statement)
          if (!loopInterruptQueue.empty) {
            def type = loopInterruptQueue.remove()
            if (type == TokenNodeType.BREAK) {
              break
            } else if (type == TokenNodeType.CONTINUE) {
              if (loops == 0) {
                throw new IllegalStatementException("Can't use CONTINUE outside of a loop", node)
              }
              //'loops' decrement will be automatically done on the amounted call
              break
            }
          }
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
        def value = evaluate(node.getChild(0))
        if (value instanceof Array) {
          if (value.type == Type.CHAR) {
            value = value.array.inject("") {s, c -> s + c}
          } else {
            value = value.array
          }
        }
        printer(value)
        break
      case TokenNodeType.FUNCTION:
      case TokenNodeType.SEQ:
      case TokenNodeType.DROP: //should have only 1 child
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
      return  e.value
    }
    switch (e.type) {
      case TokenNodeType.ARRAY:
        def list = []
        for (int i = 0; i < e.nbChildren(); i++) {
          list.add(evaluate(e.getChild(i)))
        }
        return new Array(e, list)
      case TokenNodeType.VAR_REF:
        return dequeMap.findSymbol(e.value).value
      case TokenNodeType.D_REF:
        return dequeMap.findSymbol(e.getChild(0).value).slot
      case TokenNodeType.TAB_REF:
        int index = evaluate(e.getChild(0))
        Symbol s = dequeMap.findSymbol(e.value)
        def array = s.value
        if (array.size() <= index) {
          throw new PointerException("Tried to access element at index $index of array $e.value " +
              "with size ${array.size()}", e)
        }
        return array[index]
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

  private static boolean isDefaultCaseNode(TokenNode node) {
    return node.type == TokenNodeType.CASE && node.nbChildren() == 1
  }
}
