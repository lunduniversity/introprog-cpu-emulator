package model;

public class RegStorage extends ByteStorage implements Registry {

  public RegStorage() {
    super(Registry.NUM_REGISTERS);
  }

  @Override
  public int getRegister(String name) {
    return getValueAt(Registry.nameToIdx(name));
  }

  @Override
  public void setRegister(String name, int value) {
    setValueAt(Registry.nameToIdx(name), value);
  }
}
