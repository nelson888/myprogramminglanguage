package com.tambapps.compiler

import com.tambapps.compiler.eval.EvalListener
import com.tambapps.compiler.eval.console.Console
import com.tambapps.compiler.ui.bar.Menubar
import com.tambapps.compiler.ui.model.VarsTableModel
import com.tambapps.compiler.ui.pane.CodeEditorPane
import com.tambapps.compiler.ui.bar.Toolbar
import com.tambapps.compiler.ui.panel.ConsolePanel
import com.tambapps.compiler.util.Symbol
import groovy.swing.SwingBuilder

import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JSplitPane
import javax.swing.JTable
import javax.swing.border.BevelBorder
import javax.swing.border.SoftBevelBorder
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Toolkit

class App implements EvalListener, Menubar.ViewMenuListener {

  private static final int WIDTH
  private static final int HEIGHT

  static {
    Dimension dimension = Toolkit.defaultToolkit.screenSize
    WIDTH = dimension.width * 0.5 * 0.5 as int
    HEIGHT = dimension.height * 0.6 as int
  }

  private VarsTableModel tableModel
  private JPanel tablePanel
  private JSplitPane vertSplitPane
  private JSplitPane horSplitPane
  private CodeEditorPane editorPane
  private Console console

  static void main(String[] args) {
    new App()
  }

  App() {
    def swing = new SwingBuilder()
    swing.registerBeanFactory( "myToolbar", Toolbar)
    swing.registerBeanFactory( "varsTableModel", VarsTableModel)
    swing.registerBeanFactory( "consolePanel", ConsolePanel)
    swing.registerBeanFactory( "codeEditorPane", CodeEditorPane)

    final Dimension varTableDim = [WIDTH * 0.25 as int, HEIGHT * 0.25 as int]
    swing.edt {
      frame(size: [WIDTH, HEIGHT], defaultCloseOperation: JFrame.EXIT_ON_CLOSE, show: true,
          JMenuBar: new Menubar(this)) {
        borderLayout()
        myToolbar(constraints: BorderLayout.NORTH)
        horSplitPane = splitPane(border: new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null),
            oneTouchExpandable: true, orientation: JSplitPane.HORIZONTAL_SPLIT,
            constraints: BorderLayout.CENTER) {
          vertSplitPane = splitPane(orientation : JSplitPane.VERTICAL_SPLIT) {
            ConsolePanel consolePanel = consolePanel(evalListener: this)
            console = consolePanel.console
            editorPane = codeEditorPane()
          }
          tablePanel = panel() {
            scrollPane(preferredSize : varTableDim) {
              JTable table = table(preferredScrollableViewportSize : varTableDim) {
                tableModel = varsTableModel()
              }
              tableModel.table = table
              table.columnModel.getColumn(0).maxWidth = varTableDim.width * 0.25 as int

            }
          }
        }
      }
    }

    horSplitPane.dividerLocation = 0.7
    vertSplitPane.dividerLocation = 0.7
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
    altVisibility(vertSplitPane, editorPane)
  }

  @Override
  void onTableItemClick() {
    altVisibility(horSplitPane, tablePanel)
  }

  @Override
  void onFileImported(String text) {
    editorPane.text = text
  }

  private static void altVisibility(JSplitPane splitPane, JComponent component) {
    component.visible = !component.visible
    if (component.visible) {
      splitPane.dividerSize = 10
      splitPane.dividerLocation = 0.7
    } else {
      splitPane.dividerSize = 0
    }
  }
}
