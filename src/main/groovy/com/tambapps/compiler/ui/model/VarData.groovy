package com.tambapps.compiler.ui.model

class VarData {
  String name
  def value

  VarData(String name, value) {
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
}
