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
    // Register src is specified by the operand.
    // Read the destination address from the next memory location.
    int value = reg.getValueAt(operand);
    int dst = mem.getValueAt(pc.next());
    mem.setValueAt(dst, value);
  }

  @Override
  protected String internalEvaluate(Memory mem, Registry reg, int memIdx) {
    return String.format("(src: %s)", Registry.idxToName(operand));
  }

  @Override
  public int[] getAffectedMemoryCells(Memory mem, Registry reg, ProgramCounter pc) {
    int cur = pc.getCurrentIndex();
    int dst = mem.getValueAt(cur + 1);
    return new int[] {cur, cur + 1, dst};
  }

  @Override
  public int[] getAffectedRegisters(Memory mem, Registry reg, ProgramCounter pc) {
    return new int[] {operand};
  }
}
