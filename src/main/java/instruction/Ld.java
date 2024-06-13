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
  protected String internalEvaluate(Memory mem, Registry reg, int memIdx) {
    return String.format("(dst: %s)", Registry.idxToName(operand));
  }

  @Override
  public int[] getAffectedMemoryCells(Memory mem, Registry reg, ProgramCounter pc) {
    int cur = pc.getCurrentIndex();
    return new int[] {cur, cur + 1};
  }

  @Override
  public int[] getAffectedRegisters(Memory mem, Registry reg, ProgramCounter pc) {
    return new int[] {operand};
  }
}
