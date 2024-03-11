package model;

import args.Operand;
import java.util.HashSet;
import java.util.Set;

public class ByteStorage implements ObservableStorage {

  private int[] memory;
  private Set<StorageListener> listeners = new HashSet<>();

  public ByteStorage(int size) {
    memory = new int[size];
  }

  public void setValueAt(Operand address, Operand value) {
    memory[address.getOperand()] = value.getOperand();
    notifyListeners(address, value.getOperand());
  }

  public int getValueAt(Operand address) {
    return memory[address.getOperand()];
  }

  public int size() {
    return memory.length;
  }

  public void addListener(StorageListener listener) {
    listeners.add(listener);
  }

  private void notifyListeners(Operand address, int value) {
    for (StorageListener listener : listeners) {
      listener.onMemoryChanged(address, value);
    }
  }
}
