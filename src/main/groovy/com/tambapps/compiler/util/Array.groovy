package com.tambapps.compiler.util

import com.tambapps.compiler.analyzer.token.TokenNode
import com.tambapps.compiler.exception.WrongTypeException
import com.tambapps.compiler.util.Symbol.Type
import groovy.transform.PackageScope

class Array {

  final Type type
  final def array

  @PackageScope
  Array(Type type, def array) {
    this.type = type
    this.array = array
  }

  Array(TokenNode node, Type type, int size) {
    this.type = type
    switch (type) {
      case Type.CHAR:
        array = new char[size]
        break
      case Type.INT:
        array = new int[size]
        break
      case Type.FLOAT:
        array = new float[size]
        break
      case Type.ANY:
        array = new Object[size]
        break
      default:
        throw new WrongTypeException("$type is not a type for an array", node.l, node.c)
    }
  }

  def getAt(int i) {
    return array[i]
  }

  int size() {
    return array.size()
  }

  def putAt(int index, def value) {
    array[index] = value
  }
}
