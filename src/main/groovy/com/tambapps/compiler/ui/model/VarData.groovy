package com.tambapps.compiler.ui.model

import com.tambapps.compiler.util.Symbol

class VarData {

  Symbol.Type type
  String name
  def value

  VarData(Symbol.Type type, String name, def value) {
    this.type = type
    this.name = name
    this.value = value
  }

  boolean equals(o) {
    if (this.is(o)) return true
    if (getClass() != o.class) return false

    VarData varData = (VarData) o

    if (name != varData.name) return false

    return true
  }

  int hashCode() {
    return (name != null ? name.hashCode() : 0)
  }

  def getAt(int i) {
    switch (i) {
      default:
        return type
      case 1:
        return name
      case 2:
        return value
    }
  }
}
