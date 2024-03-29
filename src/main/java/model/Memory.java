package model;

public interface Memory extends ObservableStorage {

  int size();

  String[] exportAsBinary();

  void importFromBinary(String[] data);

  String exportAsBase64();

  void importFromBase64(String base64);
}
