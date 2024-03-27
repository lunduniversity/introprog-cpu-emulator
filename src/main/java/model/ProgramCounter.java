package model;

import java.util.HashSet;
import java.util.Set;

public class ProgramCounter {

  private int currentIndex;
  private Set<ProgramCounterListener> listeners = new HashSet<>();

  public int getCurrentIndex() {
    return currentIndex;
  }

  public void setCurrentIndex(int currentIndex) {
    int oldIdx = this.currentIndex;
    this.currentIndex = currentIndex;
    notifyListeners(oldIdx, currentIndex);
  }

  public int next() {
    int next = currentIndex++;
    notifyListeners(next, currentIndex);
    return next;
  }

  public void jumpTo(int index) {
    int oldIdx = currentIndex;
    currentIndex = index;
    notifyListeners(oldIdx, currentIndex);
  }

  public void halt() {
    int oldIdx = currentIndex;
    currentIndex = -1;
    notifyListeners(oldIdx, currentIndex);
  }

  public boolean isHalted() {
    return currentIndex == -1;
  }

  public void reset() {
    int oldIdx = currentIndex;
    currentIndex = 0;
    notifyListeners(oldIdx, currentIndex);
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
