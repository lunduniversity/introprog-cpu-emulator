package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class Sub extends Instruction {

  public Sub(int operand) {
    super(InstructionFactory.INST_NAME_SUB, operand);
  }

  @Override
  protected void _execute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    int a = reg.getRegister("OP1");
    int b = reg.getRegister("OP2");
    int result = (int) (a - b);
    reg.setRegister("RES", result);
  }

  @Override
  protected String printOperand() {
    return "";
  }
}
