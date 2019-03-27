package com.tambapps.compiler.ui.panel

import com.tambapps.compiler.eval.EvalListener
import com.tambapps.compiler.eval.console.Console
import com.tambapps.compiler.ui.pane.OutputPane
import com.tambapps.compiler.ui.pane.PromptPane

import javax.swing.BoxLayout
import javax.swing.JPanel
import java.awt.Dimension

class ConsolePanel extends JPanel implements PromptPane.EnterListener {

  private final Console console
  private final OutputPane outputPane

  ConsolePanel() {
    setLayout(null)
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
    outputPane = new OutputPane()
    add(outputPane)
    PromptPane promptPane = new PromptPane(this)
    add(promptPane)
    setMinimumSize(new Dimension(0, 500))
    outputPane.setMinimumSize(new Dimension(0, 400))

    console = new Console(outputPane.&appendText, outputPane.&appendText)
  }

  @Override
  boolean onEnterClick(String text) {
    if (console.isProcessable(text)) {
      outputPane.appendText("$Console.PROMPT $text")
      console.doProcess(text)
      return true
    }
    return false
  }

  void setEvalListener(EvalListener listener) {
    console.evalListener = listener
  }

  Console getConsole() {
    return console
  }
}
