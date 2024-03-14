package io;

/** Observable IO interface. Used to expose model IO operations to views. */
public interface ObservableIO {
  void addListener(IOListener listener);
}
