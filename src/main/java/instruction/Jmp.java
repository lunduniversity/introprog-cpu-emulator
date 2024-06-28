package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class Jmp extends Instruction {

  public Jmp(int operand) {
    super(InstructionFactory.INST_NAME_JMP, operand);
  }

  @Override
  protected void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    // Operand is the register that holds the destination address.
    int dst = reg.getValueAt(operand);
    pc.jumpTo(dst);
  }

  @Override
  protected String internalPrettyPrint(Memory mem, Registry reg, int memIdx) {
    return String.format("(dst: *%s)", Registry.idxToName(operand));
  }

  @Override
  public int[] getAffectedMemoryCells(Memory mem, Registry reg, int memIdx) {
    int dst = reg.getValueAt(operand);
    return new int[] {memIdx, dst};
  }

  @Override
  public int[] getAffectedRegisters(Memory mem, Registry reg, int memIdx) {
    return new int[] {operand};
  }
}
