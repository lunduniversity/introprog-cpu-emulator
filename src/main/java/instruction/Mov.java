package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class Mov extends Instruction {

  // Move does the same as Cope, but resets the source to 0 after reading it.
  public Mov(int operand) {
    super(InstructionFactory.INST_NAME_MOV, operand);
  }

  @Override
  protected void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    // operand is the 4 right-most bits of the instruction. Out of these 4 bits, use bits 1 and 2
    // (from the left) for the source, and bits 3 and 4 for the destination, according to this rule:
    // 00: constant value (only for source, not for destination)
    // 01: register address/index
    // 10: memory address
    int srcType = (operand >> 2) & 0x3;
    int destType = (operand) & 0x3;

    // check for errors first
    if (srcType != 0b00 && srcType != 0b01 && srcType != 0b10) {
      throw new IllegalArgumentException(
          String.format("Invalid source type: %s", toBinaryString(srcType, 2)));
    }
    if (destType != 0b10 && destType != 0b01) {
      throw new IllegalArgumentException(
          String.format("Invalid destination type: %s", toBinaryString(destType, 2)));
    }

    int value = getSrcValue(srcType, mem, reg, pc);
    int dst = mem.getValueAt(pc.next());

    if (destType == 0b10) {
      mem.setValueAt(dst, value);
    } else {
      reg.setValueAt(dst, value);
    }
  }

  private int getSrcValue(int srcType, Memory mem, Registry reg, ProgramCounter pc) {
    int srcIdx = pc.next();
    int src = mem.getValueAt(srcIdx);
    if (srcType == 0b01) {
      int value = reg.getValueAt(src);
      reg.setValueAt(src, 0);
      return value;
    } else if (srcType == 0b10) {
      int value = mem.getValueAt(src);
      mem.setValueAt(src, 0);
      return value;
    }
    mem.setValueAt(srcIdx, 0);
    return src;
  }

  @Override
  protected String printOperand() {
    return String.format("(%s | %s)", parseAddrMode(operand >> 2), parseAddrMode(operand));
  }
}
