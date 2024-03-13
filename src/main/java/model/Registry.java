package model;

public interface Registry extends ObservableStorage {

  int getRegister(int index);

  int getRegister(String name);

  void setRegister(int index, int value);

  void setRegister(String name, int value);

  int getNumRegisters();

  String[] getRegisterNames();
}
