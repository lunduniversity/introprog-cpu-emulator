package io;

/** Input/Output interface. Used internally by the CPU to communicate with the outside world. */
public interface IO {
  void print(int character);

  int modCount();
}
