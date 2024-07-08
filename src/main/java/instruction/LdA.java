package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

/**
 * Load Address instruction. Similar to Load, but after reading the next memory value, the value is
 * interpreted as a memory address, and the value at that address is loaded into the destination
 * register. The destination register is specified by the operand.
 */
public class LdA extends Instruction {

  public LdA(int operand) {
    super(InstructionFactory.INST_NAME_LDA, operand);
  }

  @Override
  protected void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    // Read the next memory value, and interpret as a memory address.
    int address = mem.getValueAt(pc.next());

    // Read the value at the memory address, and store in the destination register.
    reg.setValueAt(operand, mem.getValueAt(address));
  }

  @Override
  protected String internalPrettyPrint(Memory mem, Registry reg, int memIdx) {
    if (memIdx >= mem.size()) {
      return "(" + Instruction.INVALID_REG_CHAR + ")";
    }
    int address = mem.getValueAt(memIdx + 1);
    return String.format(
        // e.g. LDA (m[12] -> R0)
        "(m[%s] %s %s)", address, Instruction.RIGHT_ARROW_CHAR, Registry.idxToName(operand));
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
