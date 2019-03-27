package com.tambapps.compiler

import com.tambapps.compiler.eval.EvalListener
import com.tambapps.compiler.ui.bar.Menubar
import com.tambapps.compiler.ui.model.VarsTableModel
import com.tambapps.compiler.ui.pane.CodeEditorPane
import com.tambapps.compiler.ui.bar.Toolbar
import com.tambapps.compiler.ui.panel.ConsolePanel
import com.tambapps.compiler.util.Symbol
import groovy.swing.SwingBuilder

import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JSplitPane
import javax.swing.border.BevelBorder
import javax.swing.border.SoftBevelBorder
import java.awt.BorderLayout

class App implements EvalListener, Menubar.ViewMenuListener {

  private VarsTableModel tableModel
  private JPanel tablePanel

  static void main(String[] args) {
    new App()
  }

  App() {
    def swing = new SwingBuilder()
    swing.registerBeanFactory( "myToolbar", Toolbar)
    swing.registerBeanFactory( "varsTableModel", VarsTableModel)

    swing.edt {
      frame(bounds: [100, 50, 900, 700], defaultCloseOperation: JFrame.EXIT_ON_CLOSE, show: true,
          JMenuBar: new Menubar(this)) {
        borderLayout()
        myToolbar(constraints: BorderLayout.NORTH)
        splitPane(border: new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null),
            oneTouchExpandable: true, orientation: JSplitPane.VERTICAL_SPLIT,
            topComponent: new ConsolePanel(this), bottomComponent: new CodeEditorPane(),
            constraints: BorderLayout.CENTER)
        tablePanel = panel(constraints: BorderLayout.EAST) {
          scrollPane() {
            table() {
              tableModel = varsTableModel()
            }
          }
        }

      }
    }
  }

  @Override
  void onVarDecl(Symbol.Type type, String name, Object value) {
    tableModel.addRow(type, name, value)
  }

  @Override
  void onVarAssign(Symbol.Type type, String name, Object value) {
    tableModel.setValueOf(name, value)
  }

  @Override
  void onEditorItemClick() {

  }

  @Override
  void onTableItemClick() {
    tablePanel.visible = !tablePanel.visible
  }
}
