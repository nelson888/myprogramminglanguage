package com.tambapps.compiler.ui.pane

import com.tambapps.compiler.eval.console.Console
import com.tambapps.compiler.ui.style.Fonts

import javax.swing.JScrollPane
import javax.swing.JTextPane
import java.awt.*
class OutputPane extends JScrollPane implements TextColoring {

  private JTextPane textPane

  OutputPane() {
    textPane = new JTextPane()
    textPane.setFont(Fonts.CODE_FONT)
    textPane.setEditable(false)
    textPane.setMinimumSize(new Dimension(0, 500))
    textPane.document.addDocumentListener(this)
    setViewportView(textPane)
    verticalScrollBarPolicy = VERTICAL_SCROLLBAR_AS_NEEDED
    horizontalScrollBarPolicy = HORIZONTAL_SCROLLBAR_NEVER

  }

  void appendText(def data) {
    textPane.text += Console.LINE_SEPARATOR + data
  }
}