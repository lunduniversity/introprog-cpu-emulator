package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class Jne extends Instruction {

  public Jne(int operand) {
    super(InstructionFactory.INST_NAME_JNE, operand);
  }

  @Override
  protected void _execute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    // Destination address is read from register indexed by operand.
    // Values being compared are read from the registers OP1 and OP2.

    int a = reg.getRegister("OP1");
    int b = reg.getRegister("OP2");

    if (a != b) {
      int dst = reg.getValueAt(operand);
      pc.jumpTo(dst);
    }
  }

  @Override
  protected String printOperand() {
    return String.format("(dst: %s)", Registry.idxToName(operand));
  }
}
