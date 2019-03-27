package com.tambapps.compiler.ui.util

class CommandHistory {

  private final int MAX_SIZE = 50
  private final LinkedList<String> deque = new LinkedList<>()
  private int index

  void push(String command) {
    if (deque.size() >= MAX_SIZE) {
      deque.removeFirst()
    }
    deque.addLast(command)
    index = -1
  }

  String getUp() {
    return index >= deque.size() - 1 ? null : deque[++index]
  }

  String getDown() {
    return index <= 0 ? null : deque[--index]
  }

}
