package util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ObservableValue<T> {

  private T value;
  private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

  public ObservableValue(T value) {
    this.value = value;
  }

  public T get() {
    return value;
  }

  public void addListener(PropertyChangeListener listener) {
    this.pcs.addPropertyChangeListener(listener);
  }

  public void removeListener(PropertyChangeListener listener) {
    this.pcs.removePropertyChangeListener(listener);
  }

  public void set(T value) {
    T oldValue = this.value;
    this.value = value;
    this.pcs.firePropertyChange("value", oldValue, value);
  }
}
