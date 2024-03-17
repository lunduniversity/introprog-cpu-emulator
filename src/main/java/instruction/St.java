package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class St extends Instruction {

  public St(int operand) {
    super(InstructionFactory.INST_NAME__ST, operand);
  }

  @Override
  protected void _execute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    // Register src is specified by the operand.
    // Read the destination address from the next memory location.
    int value = reg.getRegister(operand);
    int dst = mem.getValueAt(pc.next());
    mem.setValueAt(dst, value);
  }
}
