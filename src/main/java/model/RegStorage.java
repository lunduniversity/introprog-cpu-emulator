package model;

public class RegStorage extends ByteStorage implements Registry {

  public RegStorage() {
    super(7);
  }

  @Override
  public int getRegister(int index) {
    return getValueAt(index);
  }

  @Override
  public int getRegister(String name) {
    return getRegister(getRegisterIndex(name));
  }

  @Override
  public void setRegister(int index, int value) {
    setValueAt(index, value);
  }

  @Override
  public void setRegister(String name, int value) {
    setRegister(getRegisterIndex(name), value);
  }

  private int getRegisterIndex(String name) {
    switch (name) {
      case "OP1":
        return 0;
      case "OP2":
        return 1;
      case "RES":
        return 2;
      case "R1":
        return 3;
      case "R2":
        return 4;
      case "R3":
        return 5;
      case "PRT":
        return 6;
      default:
        throw new IllegalArgumentException("Invalid register name: " + name);
    }
  }

  @Override
  public int getNumRegisters() {
    return 7;
  }

  @Override
  public String[] getRegisterNames() {
    return new String[] {"OP1", "OP2", "RES", "R1", "R2", "R3", "PRT"};
  }
}
