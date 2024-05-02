package model;

import java.util.HashSet;
import java.util.Set;

public class ProgramCounter {

  // Constants for halt reason
  public static final int NORMAL_HALT = 0;
  public static final int END_OF_MEMORY = -1;

  private final RegStorage registry;
  private final Set<ProgramCounterListener> listeners;
  private final int memorySize;

  private boolean isHalted;

  ProgramCounter(RegStorage registry, int memorySize) {
    this.registry = registry;
    this.listeners = new HashSet<>();
    this.memorySize = memorySize;
    this.isHalted = false;
  }

  public int getCurrentIndex() {
    return registry.getRegister(Registry.REG_PC);
  }

  public void setCurrentIndex(int currentIndex) {
    int oldIdx = registry.getRegister(Registry.REG_PC);
    registry.setRegister(Registry.REG_PC, currentIndex);
    notifyChanged(oldIdx, currentIndex);
  }

  public int next() {
    int next = registry.getRegister(Registry.REG_PC);
    if (next >= memorySize) {
      throw new IllegalStateException("Reached end of memory.");
    }
    registry.setRegister(Registry.REG_PC, next + 1);
    notifyChanged(next, next + 1);

    if (next == memorySize - 1) {
      halt(END_OF_MEMORY);
    }
    return next;
  }

  public void jumpTo(int index) {
    int oldIdx = registry.getRegister(Registry.REG_PC);
    registry.setRegister(Registry.REG_PC, index);
    notifyChanged(oldIdx, index);
  }

  public void halt(int reason) {
    isHalted = true;
    notifyHalted(reason);
  }

  public boolean isHalted() {
    return isHalted;
  }

  public void reset() {
    int oldIdx = registry.getRegister(Registry.REG_PC);
    registry.setRegister(Registry.REG_PC, 0);
    isHalted = false;
    notifyChanged(oldIdx, 0);
  }

  public void addListener(ProgramCounterListener listener) {
    listeners.add(listener);
  }

  private void notifyChanged(int oldIdx, int newIdx) {
    for (ProgramCounterListener listener : listeners) {
      listener.onProgramCounterChanged(oldIdx, newIdx);
    }
  }

  private void notifyHalted(int reason) {
    for (ProgramCounterListener listener : listeners) {
      listener.onProgramCounterHalted(reason);
    }
  }
}
