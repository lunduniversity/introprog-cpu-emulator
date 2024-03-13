package model;

import java.util.HashSet;
import java.util.Set;

public class ByteStorage implements Memory {

  private int[] store;
  private Set<StorageListener> listeners = new HashSet<>();

  public ByteStorage(int size) {
    store = new int[size & 0xFF];
  }

  @Override
  public void setValueAt(int address, int value) {
    int cleanedAddress = address & 0xFF;
    int cleanedValue = value & 0xFF;
    store[cleanedAddress] = cleanedValue;
    notifyListeners(cleanedAddress, cleanedValue);
  }

  @Override
  public int getValueAt(int address) {
    return store[address & 0xFF];
  }

  @Override
  public int size() {
    return store.length;
  }

  @Override
  public void addListener(StorageListener listener) {
    listeners.add(listener);
  }

  private void notifyListeners(int address, int value) {
    for (StorageListener listener : listeners) {
      listener.onMemoryChanged(address, value);
    }
  }
}
