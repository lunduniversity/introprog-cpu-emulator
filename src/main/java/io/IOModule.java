package io;

import java.util.HashSet;
import java.util.Set;

public class IOModule implements IO, ObservableIO {

  private Set<IOListener> listeners;
  private int modCount;

  public IOModule() {
    this.listeners = new HashSet<>();
    this.modCount = 0;
  }

  @Override
  public void print(int value) {
    modCount++;
    for (IOListener listener : listeners) {
      listener.print(value);
    }
  }

  @Override
  public void print(char character) {
    modCount++;
    for (IOListener listener : listeners) {
      listener.print(character);
    }
  }

  @Override
  public void addListener(IOListener listener) {
    listeners.add(listener);
  }

  @Override
  public int modCount() {
    return modCount;
  }
}
