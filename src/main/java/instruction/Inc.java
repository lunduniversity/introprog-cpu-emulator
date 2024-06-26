package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class Inc extends Instruction {

  public Inc(int operand) {
    super(InstructionFactory.INST_NAME_INC, operand);
  }

  @Override
  protected void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    // Operand is a register index, to be incremented.
    reg.setValueAt(operand, reg.getValueAt(operand) + 1);
  }

  @Override
  protected String internalPrettyPrint(Memory mem, Registry reg, int memIdx) {
    return String.format("(%s)", Registry.idxToName(operand));
  }

  @Override
  public int[] getAffectedMemoryCells(Memory mem, Registry reg, int memIdx) {
    return new int[] {memIdx};
  }

  @Override
  public int[] getAffectedRegisters(Memory mem, Registry reg, int memIdx) {
    if (operand >= 0 && operand < Registry.NUM_REGISTERS) {
      return new int[] {operand};
    }
    return new int[0];
  }
}
