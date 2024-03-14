package io;

import java.util.HashSet;
import java.util.Set;

public class IOModule implements IO, ObservableIO {

  private Set<IOListener> listeners;

  public IOModule() {
    this.listeners = new HashSet<>();
  }

  @Override
  public void print(int character) {
    for (IOListener listener : listeners) {
      listener.print(character);
    }
  }

  @Override
  public void addListener(IOListener listener) {
    listeners.add(listener);
  }
}
