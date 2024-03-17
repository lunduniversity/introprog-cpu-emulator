package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class LdA extends Instruction {

  public LdA(int operand) {
    super(InstructionFactory.INST_NAME__LD, operand);
  }

  @Override
  protected void _execute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    int address = mem.getValueAt(pc.next());
    reg.setRegister(operand, mem.getValueAt(address));
  }
}
