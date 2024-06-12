package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class Jeq extends Instruction {

  public Jeq(int operand) {
    super(InstructionFactory.INST_NAME_JEQ, operand);
  }

  @Override
  protected void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    // Read next value, and split into two 4-bit operands.
    int value = mem.getValueAt(pc.next());
    int op1 = value >> 4;
    int op2 = value & 0xF;

    // Read the next value, use as the destination address.
    int dst = mem.getValueAt(pc.next());

    int a = reg.getValueAt(op1);
    int b = reg.getValueAt(op2);

    if (a == b) {
      pc.jumpTo(dst);
    }
  }

  @Override
  protected String printOperand() {
    return "";
  }

  @Override
  public int[] getAffectedMemoryCells(Memory mem, Registry reg, ProgramCounter pc) {
    int cur = pc.getCurrentIndex();

    int value = mem.getValueAt(cur + 1);
    int op1 = value >> 4;
    int op2 = value & 0xF;

    int dst = mem.getValueAt(cur + 2);

    int a = reg.getValueAt(op1);
    int b = reg.getValueAt(op2);

    if (a == b) {
      return new int[] {cur, cur + 1, cur + 2, dst};
    }

    return new int[] {cur, cur + 1, cur + 2};
  }

  @Override
  public int[] getAffectedRegisters(Memory mem, Registry reg, ProgramCounter pc) {
    int cur = pc.getCurrentIndex();

    int value = mem.getValueAt(cur + 1);
    int op1 = value >> 4;
    int op2 = value & 0xF;

    return new int[] {op1, op2};
  }
}
