package instruction;

import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class Jmp extends Instruction {

  public Jmp(int operand) {
    super(InstructionFactory.INST_NAME_JMP, operand);
  }

  @Override
  public void execute(Memory mem, Registry reg, ProgramCounter pc) {
    pc.jumpTo(pc.next());
  }
}
