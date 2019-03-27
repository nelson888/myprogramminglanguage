package com.tambapps.compiler.ui.bar

import javax.swing.JFileChooser
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class Menubar extends JMenuBar {

  interface ViewMenuListener {
    void onEditorItemClick()
    void onTableItemClick()
    void onFileImported(String text)
  }

  private ViewMenuListener listener

  Menubar(ViewMenuListener listener) {
    this.listener = listener
    add(fileMenu())
    add(viewMenu())
  }

  private JMenu fileMenu() {
    JMenu menu = new JMenu("File")
    JMenuItem importFile = new JMenuItem("Import file...")
    importFile.addMouseListener(new MouseAdapter() {
      @Override
      void mousePressed(MouseEvent e) {
        JFileChooser chooser = new JFileChooser("Pick a file")
        if (chooser.showOpenDialog(e.source) == JFileChooser.APPROVE_OPTION) {
          listener.onFileImported(chooser.selectedFile.text)
        }
      }
    })
    menu.add(importFile)
    return menu
  }

  private JMenu viewMenu() {
    JMenu menu = new JMenu("View")
    JMenuItem showEditor = new JMenuItem("Show/hide text editor")
    JMenuItem showTable = new JMenuItem("Show/hide variable table")
    showEditor.addMouseListener(new MouseAdapter() {
      @Override
      void mousePressed(MouseEvent e) {
        listener.onEditorItemClick()
      }
    })
    showTable.addMouseListener(new MouseAdapter() {
      @Override
      void mouseReleased(MouseEvent mouseEvent) {
        listener.onTableItemClick()
      }
    })
    menu.add(showEditor)
    menu.add(showTable)
    return menu
  }
}
