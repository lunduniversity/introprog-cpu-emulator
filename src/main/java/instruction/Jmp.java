package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class Jmp extends Instruction {

  public Jmp(int operand) {
    super(InstructionFactory.INST_NAME_JMP, operand);
  }

  @Override
  protected void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    // Read the next value, use as the destination address.
    int dst = mem.getValueAt(pc.next());
    pc.jumpTo(dst);
  }

  @Override
  protected String internalEvaluate(Memory mem, Registry reg, int memIdx) {
    int dst = mem.getValueAt(memIdx + 1);
    return String.format("(dst: %d)", dst);
  }

  @Override
  public int[] getAffectedMemoryCells(Memory mem, Registry reg, ProgramCounter pc) {
    int cur = pc.getCurrentIndex();
    int dst = mem.getValueAt(cur + 1);
    return new int[] {cur, cur + 1, dst};
  }

  @Override
  public int[] getAffectedRegisters(Memory mem, Registry reg, ProgramCounter pc) {
    return new int[0];
  }
}
