package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class LdA extends Instruction {

  public LdA(int operand) {
    super(InstructionFactory.INST_NAME_LDA, operand);
  }

  @Override
  protected void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    int address = mem.getValueAt(pc.next());
    reg.setValueAt(operand, mem.getValueAt(address));
  }

  @Override
  protected String printOperand() {
    return String.format("(dst: %s)", Registry.idxToName(operand));
  }

  @Override
  public int[] getAffectedMemoryCells(Memory mem, Registry reg, ProgramCounter pc) {
    int cur = pc.getCurrentIndex();
    int address = mem.getValueAt(cur + 1);
    return new int[] {cur, cur + 1, address};
  }

  @Override
  public int[] getAffectedRegisters(Memory mem, Registry reg, ProgramCounter pc) {
    return new int[] {operand};
  }
}
