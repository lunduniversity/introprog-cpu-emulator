package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class Ld extends Instruction {

  public Ld(int operand) {
    super(InstructionFactory.INST_NAME_LOD, operand);
  }

  @Override
  protected void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    reg.setValueAt(operand, mem.getValueAt(pc.next()));
  }

  @Override
  protected String internalPrettyPrint(Memory mem, Registry reg, int memIdx) {
    if (memIdx >= mem.size()) {
      return "(" + Instruction.INVALID_REG_CHAR + ")";
    }
    int value = mem.getValueAt(memIdx + 1);
    return String.format(
        "(%d %s %s)", value, Instruction.RIGHT_ARROW_CHAR, Registry.idxToName(operand));
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
    if (operand >= 0 && operand < Registry.NUM_REGISTERS) {
      return new int[] {operand};
    }
    return new int[0];
  }
}
