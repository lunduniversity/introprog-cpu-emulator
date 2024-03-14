package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class Hlt extends Instruction {

  public Hlt(int operand) {
    super(InstructionFactory.INST_NAME_HLT, operand);
  }

  @Override
  public void execute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    pc.halt();
  }
}
