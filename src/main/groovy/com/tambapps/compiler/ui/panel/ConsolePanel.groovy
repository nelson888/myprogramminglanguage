package com.tambapps.compiler.ui.panel

import com.tambapps.compiler.ui.pane.OutputPane
import com.tambapps.compiler.ui.pane.PromptPane

import javax.swing.BoxLayout
import javax.swing.JPanel
import java.awt.Dimension

class ConsolePanel extends JPanel {

  ConsolePanel() {
    setLayout(null)
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
    OutputPane outputPane = new OutputPane()

    add(outputPane)
    PromptPane promptPane = new PromptPane()
    add(promptPane)
    setMinimumSize(new Dimension(0, 500))
    outputPane.setMinimumSize(new Dimension(0, 400))

  }
}
