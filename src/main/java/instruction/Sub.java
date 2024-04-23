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
  protected void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    int a = reg.getRegister(Registry.REG_OP1);
    int b = reg.getRegister(Registry.REG_OP2);
    int result = a - b;
    reg.setRegister(Registry.REG_RES, result);
  }

  @Override
  protected String printOperand() {
    return "";
  }
}
