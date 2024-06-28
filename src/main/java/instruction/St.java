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
    // Read the next memory value, and split into two 4-bit parts.
    int addresses = mem.getValueAt(pc.next());
    int src = (addresses >> 4) & 0xF;
    int dst = addresses & 0xF;

    int value = reg.getValueAt(src);
    mem.setValueAt(dst, value);
  }

  @Override
  protected String internalPrettyPrint(Memory mem, Registry reg, int memIdx) {
    // Read the next memory value, and split into two 4-bit parts.
    int addresses = mem.getValueAt(memIdx + 1);
    int src = (addresses >> 4) & 0xF;
    int dst = addresses & 0xF;

    return String.format(
        "(%s %s *%s)",
        Registry.idxToName(src), Instruction.RIGHT_ARROW_CHAR, Registry.idxToName(dst));
  }

  @Override
  public int[] getAffectedMemoryCells(Memory mem, Registry reg, int memIdx) {
    int dst = mem.getValueAt(memIdx + 1);
    return new int[] {memIdx, memIdx + 1, dst};
  }

  @Override
  public int[] getAffectedRegisters(Memory mem, Registry reg, int memIdx) {
    return new int[] {operand};
  }
}
