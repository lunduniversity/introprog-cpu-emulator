package model;

public interface ObservableStorage {
  void addListener(StorageListener listener);

  void setValueAt(int address, int value);

  int setRange(int startIdx, int[] values);

  int getValueAt(int address);

  int[] getRange(int startIdx, int endIdx);

  boolean moveCellsUp(int startIdx, int endIdx);

  boolean moveCellsDown(int startIdx, int endIdx);

  void deleteCells(int startIdx, int endIdx);

  void reset();
}
