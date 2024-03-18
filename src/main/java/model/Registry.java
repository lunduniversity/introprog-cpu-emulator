package model;

public interface Registry extends ObservableStorage {

  static final String[] REGISTER_NAMES = {"OP1", "OP2", "RES", "R1", "R2", "R3", "PRT"};
  static final int NUM_REGISTERS = REGISTER_NAMES.length;
  static final String INVALID_REGISTER = "\u2013";

  int getRegister(int index);

  int getRegister(String name);

  void setRegister(int index, int value);

  void setRegister(String name, int value);

  static String idxToName(int idx) {
    if (idx >= 0 && idx < REGISTER_NAMES.length) return REGISTER_NAMES[idx];
    else return INVALID_REGISTER;
  }

  static int nameToIdx(String name) {
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
}
