package instruction;

import args.Operand;
import model.AddressableStorage;
import model.ProgramCounter;

public class Mov extends Instruction {

  public Mov(int operand) {
    super(InstructionFactory.INST_NAME_MOV, operand);
  }

  @Override
  public void execute(AddressableStorage mem, ProgramCounter pc) {
    int a = mem.getValueAt(Operand.of(pc.next()));
    mem.setValueAt(Operand.of(pc.next()), Operand.of(a));
  }
}
