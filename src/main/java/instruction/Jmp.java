package instruction;

import model.AddressableStorage;
import model.ProgramCounter;

public class Jmp extends Instruction {

  public Jmp(int operand) {
    super(InstructionFactory.INST_NAME_JMP, operand);
  }

  @Override
  public void execute(AddressableStorage mem, ProgramCounter pc) {
    pc.jumpTo(pc.next());
  }
}
