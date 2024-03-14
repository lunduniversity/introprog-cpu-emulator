package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public abstract class Instruction {

  protected final String name;
  protected final int operand;

  public Instruction(String name, int operand) {
    this.name = name;
    this.operand = operand;
  }

  public String getName() {
    return name;
  }

  public abstract void execute(Memory mem, Registry reg, ProgramCounter pc, IO io);
}
