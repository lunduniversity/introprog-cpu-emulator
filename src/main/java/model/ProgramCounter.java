package model;

import java.util.HashSet;
import java.util.Set;

public class ProgramCounter {

  private final Registry registry;
  private final Set<ProgramCounterListener> listeners;

  ProgramCounter(Registry registry) {
    this.registry = registry;
    listeners = new HashSet<>();
  }

  public int getCurrentIndex() {
    return registry.getRegister("PC");
  }

  public void setCurrentIndex(int currentIndex) {
    int oldIdx = registry.getRegister("PC");
    registry.setRegister("PC", currentIndex);
    notifyListeners(oldIdx, currentIndex);
  }

  public int next() {
    int next = registry.getRegister("PC");
    registry.setRegister("PC", next + 1);
    notifyListeners(next, next + 1);
    return next;
  }

  public void jumpTo(int index) {
    int oldIdx = registry.getRegister("PC");
    registry.setRegister("PC", index);
    notifyListeners(oldIdx, index);
  }

  public void halt() {
    int oldIdx = registry.getRegister("PC");
    registry.setRegister("PC", -1);
    notifyListeners(oldIdx, -1);
  }

  public boolean isHalted() {
    return registry.getRegister("PC") == -1;
  }

  public void reset() {
    int oldIdx = registry.getRegister("PC");
    registry.setRegister("PC", 0);
    notifyListeners(oldIdx, 0);
  }

  public void addListener(ProgramCounterListener listener) {
    listeners.add(listener);
  }

  private void notifyListeners(int oldIdx, int newIdx) {
    for (ProgramCounterListener listener : listeners) {
      listener.onProgramCounterChanged(oldIdx, newIdx);
    }
  }
}
