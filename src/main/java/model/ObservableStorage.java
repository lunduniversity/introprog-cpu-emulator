package model;

public interface ObservableStorage extends AddressableStorage {

  public int size();

  public void addListener(StorageListener listener);
}
