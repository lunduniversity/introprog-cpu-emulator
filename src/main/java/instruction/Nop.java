package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class Nop extends Instruction {

  public Nop(int operand) {
    super(InstructionFactory.INST_NAME_NOP, operand);
  }

  @Override
  protected void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    // Do nothing
  }

  @Override
  protected String printOperand() {
    return "";
  }

  @Override
  public int[] getAffectedMemoryCells(Memory mem, Registry reg, ProgramCounter pc) {
    return new int[] {pc.getCurrentIndex()};
  }

  @Override
  public int[] getAffectedRegisters(Memory mem, Registry reg, ProgramCounter pc) {
    return new int[0];
  }
}
