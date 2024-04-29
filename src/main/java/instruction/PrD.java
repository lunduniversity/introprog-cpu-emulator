package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

/** Print digit instruction. Prints the value of the OUT register. */
public class PrD extends Instruction {

  public PrD(int operand) {
    super(InstructionFactory.INST_NAME_PRD, operand);
  }

  @Override
  protected void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    int value = reg.getRegister(Registry.REG_OUT);
    io.print(value);
  }

  @Override
  protected String printOperand() {
    return "";
  }

  @Override
  public int[] getAffectedMemoryCells(Memory mem, Registry reg, ProgramCounter pc) {
    return new int[] {pc.getCurrentIndex()};
  }

  @Override
  public int[] getAffectedRegisters(Memory mem, Registry reg, ProgramCounter pc) {
    return new int[] {Registry.nameToIdx(Registry.REG_OUT)};
  }
}
