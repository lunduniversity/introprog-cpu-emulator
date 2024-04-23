package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class Hlt extends Instruction {

  public Hlt(int operand) {
    super(InstructionFactory.INST_NAME_HLT, operand, false);
  }

  @Override
  protected void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    pc.halt();
    io.print('\n');
  }

  @Override
  protected String printOperand() {
    return "";
  }
}
