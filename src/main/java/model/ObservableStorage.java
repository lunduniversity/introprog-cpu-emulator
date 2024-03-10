package model;

public interface ObservableStorage extends AddressableStorage {

    public void addListener(StorageListener listener);

}
