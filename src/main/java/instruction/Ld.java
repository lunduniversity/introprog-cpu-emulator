package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class Ld extends Instruction {

  public Ld(int operand) {
    super(InstructionFactory.INST_NAME__LD, operand);
  }

  @Override
  public void execute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    reg.setRegister(operand, mem.getValueAt(pc.next()));
  }
}
