package com.tambapps.compiler

import com.tambapps.compiler.eval.EvalListener
import com.tambapps.compiler.ui.bar.Menubar
import com.tambapps.compiler.ui.pane.CodeEditorPane
import com.tambapps.compiler.ui.bar.Toolbar
import com.tambapps.compiler.ui.panel.ConsolePanel
import groovy.swing.SwingBuilder

import javax.swing.JFrame
import javax.swing.JSplitPane
import javax.swing.border.BevelBorder
import javax.swing.border.SoftBevelBorder
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableModel
import java.awt.BorderLayout

class App implements EvalListener {

  private DefaultTableModel tableModel

  static void main(String[] args) {
    new App()
  }

  App() {
    def swing = new SwingBuilder()
    swing.registerBeanFactory( "myToolbar", Toolbar)

    swing.edt {
      frame(bounds: [100, 50, 900, 700], defaultCloseOperation: JFrame.EXIT_ON_CLOSE, show: true,
          JMenuBar: new Menubar()) {
        myToolbar(constraints: BorderLayout.NORTH)
        splitPane(border: new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null),
            oneTouchExpandable: true, orientation: JSplitPane.VERTICAL_SPLIT,
            topComponent: new ConsolePanel(this), bottomComponent: new CodeEditorPane(),
            constraints: BorderLayout.CENTER)
      }
    }
  }

  @Override
  void onVarDecl(String name, Object value) {

  }

  @Override
  void onVarAssign(String name, Object value) {

  }
}
