package com.tambapps.compiler.ui.model

import javax.swing.table.AbstractTableModel

class MyVarsTableModel extends AbstractTableModel {

  private int rows = 0

  private List<VarData> varDatas

  void addRow(VarData varData) {
    varDatas.add(varData)
    rows++
    fireTableRowsInserted(rows - 1, rows)
  }

  @Override
  int getRowCount() {
    return rows
  }

  @Override
  int getColumnCount() {
    return 2
  }

  @Override
  Object getValueAt(int i, int i1) {
    VarData v = varDatas.get(i)
    return i == 0 ? v.name : v.value
  }

  void setValueOf(String name, def value) {
    int i = varDatas.indexOf(new VarData(name, null))
    if (i < 0) {
      return
    }
    varDatas.get(i).value = value
    fireTableRowsUpdated(i, i + 1)
  }

  void remove(String name) {
    int i = varDatas.indexOf(new VarData(name, null))
    if (i < 0) {
      return
    }
    varDatas.remove(i)
    fireTableRowsDeleted(i, i+1)
  }
}
