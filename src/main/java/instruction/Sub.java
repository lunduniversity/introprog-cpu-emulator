package instruction;

import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class Sub extends Instruction {

  public Sub(int operand) {
    super(InstructionFactory.INST_NAME_SUB, operand);
  }

  @Override
  public void execute(Memory mem, Registry reg, ProgramCounter pc) {
    int a = reg.getRegister("OP1");
    int b = reg.getRegister("OP2");
    int result = (int) (a - b);
    reg.setRegister("RES", result);
  }
}
