package instruction;

import args.Operand;
import model.AddressableStorage;
import model.ProgramCounter;

public class Jne extends Instruction {

  public Jne(int operand) {
    super(InstructionFactory.INST_NAME_JNE, operand);
  }

  @Override
  public void execute(AddressableStorage mem, ProgramCounter pc) {
    int a = mem.getValueAt(Operand.of(pc.next()));
    int b = mem.getValueAt(Operand.of(pc.next()));
    if (a != b) {
      pc.jumpTo(pc.next());
    } else {
      pc.next();
    }
  }
}
