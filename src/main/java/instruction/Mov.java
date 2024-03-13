package instruction;

import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class Mov extends Instruction {

  public Mov(int operand) {
    super(InstructionFactory.INST_NAME_MOV, operand);
  }

  @Override
  public void execute(Memory mem, Registry reg, ProgramCounter pc) {
    // operand is the 4 right-most bits of the instruction. Out of these 4 bits, use bits 1 and 2 to
    // determine if the source and destination are registers or memory. The first bit is the source
    // and the second bit is the destination, and the value is 0 for memory and 1 for register
    int srcType = (operand >> 3) & 1;
    int destType = (operand >> 2) & 1;
    int src = mem.getValueAt(pc.next());
    int dst = mem.getValueAt(pc.next());
    int value = srcType == 0 ? mem.getValueAt(src) : reg.getRegister(src);

    if (destType == 0) {
      mem.setValueAt(dst, value);
    } else {
      reg.setRegister(dst, value);
    }
  }
}
