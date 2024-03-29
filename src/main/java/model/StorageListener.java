package model;

public interface StorageListener {

  void onStorageChanged(int startIdx, int[] values);
}
