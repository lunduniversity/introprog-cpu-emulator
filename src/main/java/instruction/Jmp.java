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
  protected void _execute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    // Destination address is read from register indexed by operand.
    int dst = reg.getRegister(operand);
    pc.jumpTo(dst);
  }

  @Override
  protected String printOperand() {
    return String.format("(dst: %s)", Registry.idxToName(operand));
  }
}
