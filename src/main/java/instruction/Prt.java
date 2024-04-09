package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

/** Print instruction. Prints the value of the PRT register. */
public class Prt extends Instruction {

  public Prt(int operand) {
    super(InstructionFactory.INST_NAME_PRT, operand);
  }

  @Override
  protected void _execute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    int value = reg.getRegister("PRT");
    io.print(value);
  }

  @Override
  protected String printOperand() {
    return "";
  }
}
