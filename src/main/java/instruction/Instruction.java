package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public abstract class Instruction {

  public static final String INVALID_ADDR_CHAR = "\u2013"; // en dash
  public static final String INVALID_REG_CHAR = "\u26A0"; // warning sign
  public static final String INVALID_OPERATOR_CHAR = "\u26A0"; // warning sign

  static final String EQUAL_CHAR = "\u003d"; // equals sign
  static final String NOT_EQUAL_CHAR = "\u2260"; // not equal to sign
  static final String LESS_THAN_CHAR = "\u003C"; // less than sign
  static final String GREATER_THAN_CHAR = "\u003E"; // greater than sign
  static final String LESS_THAN_OR_EQUAL_TO_CHAR = "\u2264"; // less than or equal to sign
  static final String GREATER_THAN_OR_EQUAL_TO_CHAR = "\u2265"; // greater than or equal to sign

  static final String RIGHT_ARROW_CHAR = "\u2192";

  static final byte ADDR_TYPE_CONSTANT = 0b00;
  static final byte ADDR_TYPE_REGISTER = 0b01;
  static final byte ADDR_TYPE_MEMORY = 0b10;
  static final byte ADDR_TYPE_INVALID = 0b11;

  protected final String name;
  protected final int operand;
  protected final boolean autoIncrement;

  protected Instruction(String name, int operand) {
    this(name, operand, true);
  }

  protected Instruction(String name, int operand, boolean autoIncrement) {
    this.name = name;
    this.operand = operand;
    this.autoIncrement = autoIncrement;
  }

  public String prettyPrint(Memory mem, Registry reg, int memIdx) {
    return String.format("%s %s", name, internalPrettyPrint(mem, reg, memIdx)).trim();
  }

  protected abstract String internalPrettyPrint(Memory mem, Registry reg, int memIdx);

  public final void execute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    if (autoIncrement) {
      pc.next();
    }
    internalExecute(mem, reg, pc, io);
  }

  protected abstract void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io);

  @Override
  public String toString() {
    return name;
  }

  /**
   * Get the indices of all memory cells affected by this instruction. This is used to highlight the
   * affected memory cells in the GUI.
   *
   * @return an array of memory cell indices, which will always include at least itself.
   */
  public abstract int[] getAffectedMemoryCells(Memory mem, Registry reg, int memIdx);

  /**
   * Get the indices of all registers affected by this instruction. This is used to highlight the
   * affected registers in the GUI.
   *
   * @return an array of register indices, which may be empty.
   */
  public abstract int[] getAffectedRegisters(Memory mem, Registry reg, int memIdx);

  /**
   * Parse the addressing mode of an operand. The mode is a 2-bit value, and must occupy the last
   * two bits of the operand. 00 = VAL (constant), 01 = REG (register), 10 = MEM (memory).
   *
   * @param operand
   * @return
   */
  static String parseAddrMode(int operand) {
    int mode = operand & 0x3; // Pick the last 2 bits
    switch (mode) {
      case 0:
        return "VAL";
      case 1:
        return "REG";
      case 2:
        return "MEM";
      default:
        return INVALID_ADDR_CHAR;
    }
  }

  public static String toBinaryString(int value, int length) {
    return toBinaryString(value, length, -1);
  }

  public static String toBinaryString(int value, int length, int groupSize) {
    String formatSpecifier = String.format("%%%ds", length);
    String bin = String.format(formatSpecifier, Integer.toBinaryString(value)).replace(' ', '0');
    if (groupSize > 1 && bin.length() > groupSize) {
      // Insert spaces every groupSize characters
      StringBuilder sb = new StringBuilder(bin);
      for (int i = groupSize; i < sb.length(); i += groupSize + 1) {
        sb.insert(i, ' ');
      }
      return sb.toString();
    }
    return bin;
  }
}
