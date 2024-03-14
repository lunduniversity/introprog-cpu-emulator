package model;

public interface ObservableStorage {
  void addListener(StorageListener listener);

  void reset();
}
