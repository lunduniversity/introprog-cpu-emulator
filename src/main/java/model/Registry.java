package model;

public interface Registry extends ObservableStorage {

  static final String REG_R0 = "R0";
  static final String REG_R1 = "R1";
  static final String REG_R2 = "R2";
  static final String REG_OP1 = "OP1";
  static final String REG_OP2 = "OP2";
  static final String REG_RES = "RES";
  static final String REG_OUT = "OUT";
  static final String REG_PC = "PC";
  static final String[] REGISTER_NAMES = {
    REG_R0, REG_R1, REG_R2, REG_OP1, REG_OP2, REG_RES, REG_OUT, REG_PC
  };
  static final int NUM_REGISTERS = REGISTER_NAMES.length;
  static final String INVALID_REGISTER = "\u2013";

  int getRegister(String name);

  void setRegister(String name, int value);

  static String idxToName(int idx) {
    if (idx >= 0 && idx < REGISTER_NAMES.length) return REGISTER_NAMES[idx];
    else return INVALID_REGISTER;
  }

  static int nameToIdx(String name) {
    for (int i = 0; i < REGISTER_NAMES.length; i++) {
      if (REGISTER_NAMES[i].equals(name)) return i;
    }
    throw new IllegalArgumentException("Invalid register name: " + name);
  }

  static boolean isValidIndex(int idx) {
    return idx >= 0 && idx < NUM_REGISTERS;
  }
}
