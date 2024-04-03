package model;

public class RegStorage extends ByteStorage implements Registry {

  public RegStorage() {
    super(Registry.NUM_REGISTERS);
  }

  @Override
  public int getRegister(String name) {
    int value = getValueAt(Registry.nameToIdx(name));
    // Sign-extend the byte value to an int
    return (byte) value;
  }

  @Override
  public void setRegister(String name, int value) {
    setValueAt(Registry.nameToIdx(name), value);
  }
}
