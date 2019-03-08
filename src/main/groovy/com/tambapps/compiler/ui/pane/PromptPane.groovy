package com.tambapps.compiler.ui.pane

import com.tambapps.compiler.ui.style.Fonts

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
    if (keyEvent.keyCode == KeyEvent.VK_ENTER) {
      if (enterListener.onEnterClick(text)) {
        text = ""
      }
    }
  }
}