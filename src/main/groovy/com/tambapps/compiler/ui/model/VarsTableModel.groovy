package com.tambapps.compiler.ui.model

import com.tambapps.compiler.util.Symbol

import javax.swing.JOptionPane
import javax.swing.JTable
import javax.swing.table.AbstractTableModel

class VarsTableModel extends AbstractTableModel {

  private static final String[] COLUMN_TITLES = ['type', 'variable', 'value']

  private final List<VarData> varDatas = []

  void addRow(Symbol.Type type, String name, Object value) {
    varDatas.add(new VarData(type, name, value))
    fireTableRowsInserted(rowCount - 1, rowCount)
  }

  @Override
  int getRowCount() {
    return varDatas.size()
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

  @Override
  boolean isCellEditable(int i, int j) {
    return j == 2 && varDatas[i].type != Symbol.Type.ANY
  }

  @Override
  void setValueAt(Object o, int i, int j) {
    VarData v = varDatas[i]
    try {
      v.value = v.type.parse(o)
      //TODO change variable value on console
    } catch (IllegalArgumentException e) {
      JOptionPane.showMessageDialog(null,
          "'$o' is not well formated",
          'Format error',
          JOptionPane.ERROR_MESSAGE)
    }
  }

}
