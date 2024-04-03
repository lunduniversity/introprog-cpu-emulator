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
  protected void _execute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    // Do nothing
  }

  @Override
  protected String printOperand() {
    return "";
  }
}
