package com.tambapps.compiler

import com.tambapps.compiler.ui.bar.Menubar
import com.tambapps.compiler.ui.pane.CodeEditorPane
import com.tambapps.compiler.ui.bar.Toolbar
import com.tambapps.compiler.ui.panel.ConsolePanel

import javax.swing.JFrame
import javax.swing.JSplitPane
import javax.swing.JTextPane
import javax.swing.border.BevelBorder
import javax.swing.border.SoftBevelBorder
import java.awt.BorderLayout


JFrame frame = new JFrame()
frame.setJMenuBar(new Menubar())

frame.setBounds(100, 50, 900, 700)
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)

frame.getContentPane().add(new Toolbar(), BorderLayout.NORTH);

JSplitPane splitPane = new JSplitPane()
splitPane.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null))
splitPane.setOneTouchExpandable(true)
splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT)
frame.getContentPane().add(splitPane, BorderLayout.CENTER)

ConsolePanel consolePanel = new ConsolePanel()
JTextPane outputPane = new JTextPane()
outputPane.setEditable(false)
splitPane.setTopComponent(consolePanel)
CodeEditorPane editorPane = new CodeEditorPane()
splitPane.setBottomComponent(editorPane)


frame.setVisible(true)