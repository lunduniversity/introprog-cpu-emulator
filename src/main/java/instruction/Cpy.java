package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class Cpy extends Instruction {

  public Cpy(int operand) {
    super(InstructionFactory.INST_NAME_CPY, operand);
  }

  @Override
  protected void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    // Copy always operates on registers, not memory.
    // The right-most operand bit is used alter between "copy" and "move".
    // The next memory cell is split into two 4-bit parts, each representing the source and
    // destination register index.

    // 0 = copy, 1 = move
    boolean isMove = (operand & 0x1) == 1;

    int value = mem.getValueAt(pc.next());
    int src = (value >> 4) & 0xF;
    int dst = value & 0xF;

    reg.setValueAt(dst, reg.getValueAt(src));
    if (isMove) {
      reg.setValueAt(src, 0);
    }
  }

  @Override
  protected String internalPrettyPrint(Memory mem, Registry reg, int memIdx) {
    if (memIdx + 1 >= mem.size()) {
      return String.format("(%s %s %s)", INVALID_REG_CHAR, RIGHT_ARROW_CHAR, INVALID_REG_CHAR);
    }
    int value = mem.getValueAt(memIdx + 1);
    int src = (value >> 4) & 0xF;
    int dst = value & 0xF;
    return String.format(
        "(%s %s %s)", Registry.idxToName(src), RIGHT_ARROW_CHAR, Registry.idxToName(dst));
  }

  @Override
  public int[] getAffectedMemoryCells(Memory mem, Registry reg, int memIdx) {
    if (memIdx >= mem.size()) {
      return new int[] {memIdx};
    }
    return new int[] {memIdx, memIdx + 1};
  }

  @Override
  public int[] getAffectedRegisters(Memory mem, Registry reg, int memIdx) {
    if (memIdx >= mem.size()) {
      return new int[0];
    }
    int value = mem.getValueAt(memIdx + 1);
    int src = (value >> 4) & 0xF;
    int dst = value & 0xF;

    if (src >= 0 && src < Registry.NUM_REGISTERS && dst >= 0 && dst < Registry.NUM_REGISTERS) {
      return new int[] {src, dst};
    }
    if (src >= 0 && src < Registry.NUM_REGISTERS) {
      return new int[] {src};
    }
    if (dst >= 0 && dst < Registry.NUM_REGISTERS) {
      return new int[] {dst};
    }
    return new int[0];
  }
}
