package io;

/** Listener for IO operations. Implemented by views to observe IO operations in the model. */
public interface IOListener {
  void print(int value);

  void print(char character);
}
