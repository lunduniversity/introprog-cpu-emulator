package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

/** Print instruction as text (ADCII). Prints the value of the OUT register. */
public class PrT extends Instruction {

  public PrT(int operand) {
    super(InstructionFactory.INST_NAME_PRT, operand);
  }

  @Override
  protected void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    int value = reg.getRegister(Registry.REG_OUT);
    io.print((char) value);
  }

  @Override
  protected String printOperand() {
    return "";
  }
}
