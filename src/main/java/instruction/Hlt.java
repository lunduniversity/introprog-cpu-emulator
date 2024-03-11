package instruction;

import model.AddressableStorage;
import model.ProgramCounter;

public class Hlt extends Instruction {

  public Hlt(int operand) {
    super(InstructionFactory.INST_NAME_HLT, operand);
  }

  @Override
  public void execute(AddressableStorage mem, ProgramCounter pc) {
    pc.halt();
  }
}
