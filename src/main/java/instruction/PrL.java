package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

/**
 * Print Loop instruction. The memory address of the first character to print must be in the OP1
 * register, and the memory address of the last character to print in the OP2 register. The
 * characters are printed in the order they appear in memory, and the memory address in the OP1
 * register is incremented by 1 after each character is printed.
 */
public class PrL extends Instruction {

  public PrL(int operand) {
    super(InstructionFactory.INST_NAME_PRL, operand, false);
  }

  @Override
  protected void _execute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    int start = reg.getRegister("OP1");
    int end = reg.getRegister("OP2");

    int character = mem.getValueAt(start);
    reg.setRegister("PRT", character);
    reg.setRegister("OP1", start + 1);
    io.print(character);

    if (start == end) {
      pc.next();
    }
  }

  @Override
  protected String printOperand() {
    return "";
  }
}
