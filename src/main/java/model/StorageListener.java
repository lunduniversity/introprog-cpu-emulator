package model;

public interface StorageListener {

  void onMemoryChanged(int address, int value);
}
