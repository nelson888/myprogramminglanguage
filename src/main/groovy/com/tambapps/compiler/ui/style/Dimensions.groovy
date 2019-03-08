package com.tambapps.compiler.ui.style

import java.awt.Dimension
import java.awt.Toolkit

class Dimensions {

  static final Dimension SCREEN_SIZE = Toolkit.defaultToolkit.screenSize

  static getSCREEN_WIDTH() {
    return SCREEN_SIZE.width
  }

  static getSCREEN_HEIGHT() {
    return SCREEN_SIZE.height
  }

  static final int TEXT_SIZE = 14 //TODO adapt to screen size/resolution?
}
