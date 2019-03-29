package com.tambapps.compiler.ui.model

import javax.swing.table.AbstractTableModel

class HistoryTableModel extends AbstractTableModel {

  List<String> commands

  @Override
  int getRowCount() {
    return commands.size()
  }

  @Override
  int getColumnCount() {
    return 1
  }

  @Override
  Object getValueAt(int i, int j) {
    return commands[i]
  }

  @Override
  String getColumnName(int i) {
    return 'history'
  }

}
