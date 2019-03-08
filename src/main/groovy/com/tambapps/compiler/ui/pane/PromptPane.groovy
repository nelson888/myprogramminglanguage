package com.tambapps.compiler.ui.pane

import com.tambapps.compiler.eval.console.Console
import javafx.scene.input.KeyCode

import javax.swing.border.EtchedBorder
import javax.swing.event.DocumentEvent
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

class PromptPane extends CodePane implements KeyListener {

  Console console = new Console()

  PromptPane() {
    setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null))
    setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16))
    addKeyListener(this)
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
      console.process()
    }
  }
}