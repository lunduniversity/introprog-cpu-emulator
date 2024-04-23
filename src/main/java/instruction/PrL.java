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
  protected void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    int start = reg.getRegister(Registry.REG_OP1);
    int end = reg.getRegister(Registry.REG_OP2);

    int character = mem.getValueAt(start);
    reg.setRegister(Registry.REG_OUT, character);
    io.print(character);

    if (start == end) {
      pc.next();
    } else {
      reg.setRegister(Registry.REG_OP1, start + 1);
    }
  }

  @Override
  protected String printOperand() {
    return "";
  }
}
