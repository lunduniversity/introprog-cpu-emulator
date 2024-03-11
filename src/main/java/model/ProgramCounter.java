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
    this.currentIndex = currentIndex;
    notifyListeners();
  }

  public int next() {
    int next = currentIndex++;
    notifyListeners();
    return next;
  }

  public void jumpTo(int index) {
    currentIndex = index;
    notifyListeners();
  }

  public void halt() {
    currentIndex = -1;
    notifyListeners();
  }

  public boolean isHalted() {
    return currentIndex == -1;
  }

  public void addListener(ProgramCounterListener listener) {
    listeners.add(listener);
  }

  private void notifyListeners() {
    for (ProgramCounterListener listener : listeners) {
      listener.onProgramCounterChanged(currentIndex);
    }
  }
}
