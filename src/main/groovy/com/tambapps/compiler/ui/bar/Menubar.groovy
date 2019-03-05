package com.tambapps.compiler.ui.bar

import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class Menubar extends JMenuBar {

  Menubar() {
    add(fileMenu())
    add(viewMenu())
  }


  private static JMenu fileMenu() {
    JMenu menu = new JMenu("File")
    JMenuItem importFile = new JMenuItem("Import file...")
    importFile.addMouseListener(new MouseAdapter() {
      @Override
      void mousePressed(MouseEvent e) {
        //TODO
      }
    })
    menu.add(importFile)
    return menu
  }

  private static JMenu viewMenu() {
    JMenu menu = new JMenu("View")
    JMenuItem showEditor = new JMenuItem("Show text editor")
    showEditor.addMouseListener(new MouseAdapter() {
      @Override
      void mousePressed(MouseEvent e) {
        //TODO
      }
    })
    menu.add(showEditor)
    return menu
  }
}
