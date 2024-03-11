package instruction;

import args.Operand;
import model.AddressableStorage;
import model.ProgramCounter;

public class Add extends Instruction {

  public Add(int operand) {
    super(InstructionFactory.INST_NAME_ADD, operand);
  }

  @Override
  public void execute(AddressableStorage mem, ProgramCounter pc) {
    int a = mem.getValueAt(Operand.reg("OP1"));
    int b = mem.getValueAt(Operand.reg("OP2"));
    int result = (int) (a + b);
    mem.setValueAt(Operand.reg("RES"), Operand.of(result));
  }
}
