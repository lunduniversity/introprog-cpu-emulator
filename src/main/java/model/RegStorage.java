package model;

public class RegStorage extends ByteStorage implements Registry {

  public RegStorage() {
    super(Registry.NUM_REGISTERS);
  }

  @Override
  public int getRegister(String name) {
    int value = getValueAt(Registry.nameToIdx(name));
    return value & 0xFF;
  }

  @Override
  public void setRegister(String name, int value) {
    setValueAt(Registry.nameToIdx(name), value);
  }

  public int getRawValue(String name) {
    return getRawValueAt(Registry.nameToIdx(name));
  }
}
