package com.tambapps.compiler.ui.util


class CommandHistory {

  private static final int MAX_SIZE = 50
  private final LinkedList<String> commands //list AND deque
  private int index

  CommandHistory(LinkedList<String> commands) {
    this.commands = commands
  }

  void push(String command) {
    if (commands.size() >= MAX_SIZE) {
      commands.removeFirst()
    }
    commands.addLast(command)
    index = -1
  }

  String getUp() {
    return index >= commands.size() - 1 ? null : commands[++index]
  }

  String getDown() {
    return index <= 0 ? null : commands[--index]
  }

  String getAt(int i) {
    return commands[i]
  }

  int size() {
    return commands.size()
  }

}
