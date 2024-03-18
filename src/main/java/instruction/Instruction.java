package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public abstract class Instruction {

  static final String INVALID_ADDR_TYPE = "\u2013";

  protected final String name;
  protected final int operand;
  protected final boolean autoIncrement;

  public Instruction(String name, int operand) {
    this(name, operand, true);
  }

  public Instruction(String name, int operand, boolean autoIncrement) {
    this.name = name;
    this.operand = operand;
    this.autoIncrement = autoIncrement;
  }

  public final void execute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    if (autoIncrement) {
      pc.next();
    }
    _execute(mem, reg, pc, io);
  }

  protected abstract void _execute(Memory mem, Registry reg, ProgramCounter pc, IO io);

  @Override
  public String toString() {
    return String.format("%s %s", name, printOperand()).trim();
  }

  protected abstract String printOperand();

  /**
   * Parse the addressing mode of an operand. The mode is a 2-bit value, and must occupy the last
   * two bits of the operand. 00 = VAL (constant), 01 = REG (register), 10 = MEM (memory).
   *
   * @param operand
   * @return
   */
  protected String parseAddrMode(int operand) {
    int mode = operand & 0x3; // Pick the last 2 bits
    switch (mode) {
      case 0:
        return "VAL";
      case 1:
        return "REG";
      case 2:
        return "MEM";
      default:
        return INVALID_ADDR_TYPE;
    }
  }

  protected String toBinaryString(int value, int length) {
    return String.format("%" + length + "s", Integer.toBinaryString(value)).replace(' ', '0');
  }
}
