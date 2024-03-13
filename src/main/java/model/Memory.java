package model;

public interface Memory extends ObservableStorage {

  public void setValueAt(int address, int value);

  public int getValueAt(int address);

  public int size();
}
