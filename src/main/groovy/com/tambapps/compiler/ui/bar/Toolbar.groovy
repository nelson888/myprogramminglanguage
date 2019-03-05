package com.tambapps.compiler.ui.bar

import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JToolBar
import java.awt.Image
import java.awt.Toolkit

class Toolbar extends JToolBar {

  Toolbar() {
    JButton btnRun = new JButton()
    ImageIcon runIcon = new ImageIcon(
        Toolkit.getDefaultToolkit().getImage(Toolbar.getResource("/execute.png"))
            .getScaledInstance(24, 24, Image.SCALE_SMOOTH))
    btnRun.setIcon(runIcon)
    add(btnRun)
  }
}
