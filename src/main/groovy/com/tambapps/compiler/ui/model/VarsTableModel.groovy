package com.tambapps.compiler.ui.model

import com.tambapps.compiler.util.Symbol

import javax.swing.table.AbstractTableModel

class VarsTableModel extends AbstractTableModel {

  private static final String[] COLUMN_TITLES = ["type", "variable", "value"]

  private int rows = 0

  private final List<VarData> varDatas = []

  void addRow(Symbol.Type type, String name, Object value) {
    varDatas.add(new VarData(type, name, value))
    rows++
    fireTableRowsInserted(rows - 1, rows)
  }

  @Override
  int getRowCount() {
    return rows
  }

  @Override
  int getColumnCount() {
    return 3
  }

  @Override
  Object getValueAt(int i, int j) {
    return varDatas[i][j]
  }

  void setValueOf(String name, def value) {
    int i = varDatas.indexOf(new VarData(null, name, null))
    if (i < 0) {
      return
    }
    varDatas.get(i).value = value
    fireTableRowsUpdated(i, i + 1)
  }

  void remove(String name) {
    int i = varDatas.indexOf(new VarData(null, name, null))
    if (i < 0) {
      return
    }
    varDatas.remove(i)
    fireTableRowsDeleted(i, i+1)
  }

  @Override
  String getColumnName(int i) {
    return COLUMN_TITLES[i]
  }
}
