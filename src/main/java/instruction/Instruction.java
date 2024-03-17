package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public abstract class Instruction {

  protected final String name;
  protected final int operand;
  protected final boolean autoIncrement;

  public Instruction(String name, int operand) {
    this(name, operand, true);
  }

  public Instruction(String name, int operand, boolean autoIncrement) {
    this.name = name;
    this.operand = operand;
    this.autoIncrement = autoIncrement;
  }

  public String getName() {
    return name;
  }

  public final void execute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    if (autoIncrement) {
      pc.next();
    }
    _execute(mem, reg, pc, io);
  }

  protected abstract void _execute(Memory mem, Registry reg, ProgramCounter pc, IO io);
}
