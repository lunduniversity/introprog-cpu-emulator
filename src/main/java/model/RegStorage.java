package model;

public class RegStorage extends ByteStorage implements Registry {

  public RegStorage() {
    super(Registry.NUM_REGISTERS);
  }

  @Override
  public int getRegister(int index) {
    return getValueAt(index);
  }

  @Override
  public int getRegister(String name) {
    return getRegister(Registry.nameToIdx(name));
  }

  @Override
  public void setRegister(int index, int value) {
    setValueAt(index, value);
  }

  @Override
  public void setRegister(String name, int value) {
    setRegister(Registry.nameToIdx(name), value);
  }
}
