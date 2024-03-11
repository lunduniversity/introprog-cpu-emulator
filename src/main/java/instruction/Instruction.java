package instruction;

import model.AddressableStorage;
import model.ProgramCounter;

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

  public abstract void execute(AddressableStorage mem, ProgramCounter pc);
}
