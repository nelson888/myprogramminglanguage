package com.tambapps.compiler.util

import com.tambapps.compiler.analyzer.token.TokenNode
import com.tambapps.compiler.exception.WrongTypeException
import com.tambapps.compiler.util.Symbol.Type
import groovy.transform.PackageScope

class Array {

  Type type
  final List array = new ArrayList()
  final boolean isString

  @PackageScope
  Array(Type type, String s) {
    this.type = type
    for (int i = 0; i < s.length(); i++) {
      array.add(s.charAt(i))
    }
  }

  Array(Type type) {
    this.type = type
  }

  Array(TokenNode node, List list) {
    if (list.size() == 0) {
      type = Type.ANY
      return
    }
    Set<Type> typeSet = list.collect {value -> Type.fromValue(value) }.toSet()
    if (null in typeSet) {
      throw new RuntimeException("Error TODO $node")
    }
    type = typeSet.size() == 1 ? typeSet.iterator().next() : Type.ANY
    array.addAll(list)
  }

  Array(TokenNode node, Type type, int size) {
    this.type = type
    if (!(type in [Type.CHAR, Type.INT, Type.FLOAT, Type.ANY])) {
      throw new WrongTypeException("$type is not a type for an array", node.l, node.c)
    }
    for (int i = 0; i < size; i++) {
      array.add(type.defaultValue)
    }
  }

  def getAt(int i) {
    return array[i]
  }

  int size() {
    return array.size()
  }

  def putAt(int index, def value) {
    if (type == Type.INT && value instanceof Character) {
      array[index] = (int) value
    } else if (type == Type.CHAR && value instanceof Integer) {
      array[index] =  (char) value
    } else {
      array[index] = value
    }
  }

  void append(def value) {
    array.add(value)
  }

  @Override
  String toString() {
    return array
  }

  @Override
  boolean equals(Object o) {
    if (o instanceof String) {

    }
    return super.equals(o)
  }
}
