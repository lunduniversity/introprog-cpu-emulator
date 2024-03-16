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
  public void execute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
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
          String.format("Invalid source type: %2s", Integer.toBinaryString(srcType))
              .replace(" ", "0"));
    }
    if (destType != 0b10 && destType != 0b01) {
      throw new IllegalArgumentException(
          String.format("Invalid destination type: %2s", Integer.toBinaryString(destType))
              .replace(" ", "0"));
    }

    int src = mem.getValueAt(pc.next());
    int dst = mem.getValueAt(pc.next());
    int value = getSrcValue(srcType, src, mem, reg);

    if (destType == 0b10) {
      mem.setValueAt(dst, value);
    } else {
      reg.setRegister(dst, value);
    }
  }

  private int getSrcValue(int srcType, int src, Memory mem, Registry reg) {
    if (srcType == 0b01) {
      int value = reg.getRegister(src);
      reg.setRegister(src, 0);
      return value;
    } else if (srcType == 0b10) {
      int value = mem.getValueAt(src);
      mem.setValueAt(src, 0);
      return value;
    }
    return src;
  }
}