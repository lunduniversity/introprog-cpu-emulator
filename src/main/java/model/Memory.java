package model;

public interface Memory extends ObservableStorage {

  void setValueAt(int address, int value);

  int getValueAt(int address);

  int size();

  String exportAsBase64();

  void importFromBase64(String base64);
}
