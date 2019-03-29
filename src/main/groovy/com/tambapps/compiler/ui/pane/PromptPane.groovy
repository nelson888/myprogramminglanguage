package com.tambapps.compiler.ui.pane

import com.tambapps.compiler.ui.style.Fonts
import com.tambapps.compiler.ui.util.CommandHistory

import javax.swing.JTextField
import javax.swing.border.EtchedBorder
import java.awt.Dimension
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class PromptPane extends JTextField implements TextColoring, KeyListener {

  interface EnterListener {
    //returns whether to clear or not text
    boolean onEnterClick(String text)
  }

  final EnterListener enterListener
  CommandHistory commandHistory

  PromptPane(EnterListener enterListener) {
    this.enterListener = enterListener
    setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null))
    setFont(Fonts.CODE_FONT)
    document.addDocumentListener(this)
    addKeyListener(this)
    setMaximumSize(new Dimension(Integer.MAX_VALUE, 100))
  }

  @Override
  void keyTyped(KeyEvent keyEvent) {

  }

  @Override
  void keyPressed(KeyEvent keyEvent) {

  }

  @Override
  void keyReleased(KeyEvent keyEvent) {
    switch (keyEvent.keyCode) {
      case KeyEvent.VK_UP:
      case KeyEvent.VK_DOWN:
        String command = keyEvent.keyCode == KeyEvent.VK_UP ?
            commandHistory.getUp() : commandHistory.getDown()
        if (command != null) {
          text = command
        }
        break
      case KeyEvent.VK_ENTER:
        if (enterListener.onEnterClick(text)) {
          commandHistory.push(text)
          text = ""
        }
        break
    }

  }
}