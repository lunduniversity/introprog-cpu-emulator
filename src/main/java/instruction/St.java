package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class St extends Instruction {

  public St(int operand) {
    super(InstructionFactory.INST_NAME_STO, operand);
  }

  @Override
  protected void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    // Read the next memory value, use it as the address to store the value.
    int address = mem.getValueAt(pc.next());

    int value = reg.getValueAt(operand);
    mem.setValueAt(address, value);
  }

  @Override
  protected String internalPrettyPrint(Memory mem, Registry reg, int memIdx) {
    if (memIdx >= mem.size()) {
      return Instruction.INVALID_REG_CHAR;
    }
    // Read the next memory value, and split into two 4-bit parts.
    int address = mem.getValueAt(memIdx + 1);

    return String.format(
        "(%s %s m[%d])", Registry.idxToName(operand), Instruction.RIGHT_ARROW_CHAR, address);
  }

  @Override
  public int[] getAffectedMemoryCells(Memory mem, Registry reg, int memIdx) {
    if (memIdx >= mem.size()) {
      return new int[] {memIdx};
    }
    int address = mem.getValueAt(memIdx + 1);
    return new int[] {memIdx, memIdx + 1, address};
  }

  @Override
  public int[] getAffectedRegisters(Memory mem, Registry reg, int memIdx) {
    if (operand >= 0 && operand < Registry.NUM_REGISTERS) {
      return new int[] {operand};
    }
    return new int[0];
  }
}
