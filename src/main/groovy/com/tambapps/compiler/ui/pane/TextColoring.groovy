package com.tambapps.compiler.ui.pane

import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

trait TextColoring implements DocumentListener {

  @Override
  void insertUpdate(DocumentEvent event) {
    highlight(event)
  }

  @Override
  void removeUpdate(DocumentEvent event) {
    highlight(event)
  }

  @Override
  void changedUpdate(DocumentEvent event) {
    highlight(event)
  }


  private void highlight(DocumentEvent event) {
    //TODO
  }

}