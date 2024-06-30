package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class CJp extends Instruction {

  static final int EQUAL = 0x1;
  static final int NOT_EQUAL = 0x2;
  static final int LESS_THAN = 0x3;
  static final int GREATER_THAN = 0x4;
  static final int LESS_THAN_OR_EQUAL = 0x5;
  static final int GREATER_THAN_OR_EQUAL = 0x6;

  public CJp(int operand) {
    super(InstructionFactory.INST_NAME_CJP, operand);
  }

  @Override
  protected void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    // Operand specifies the comparison operation. See #evaluateCondition for details.

    // Read next value, and split into two 4-bit operands.
    int value = mem.getValueAt(pc.next());
    int op1 = value >> 4;
    int op2 = value & 0xF;

    // Read destination from the RES register.
    int dst = reg.getRegister(Registry.REG_RES);

    int a = reg.getValueAt(op1);
    int b = reg.getValueAt(op2);

    if (evaluateCondition(operand, a, b)) {
      pc.jumpTo(dst);
    }
  }

  private boolean evaluateCondition(int operand, int a, int b) {
    switch (operand) {
      case EQUAL:
        return a == b;
      case NOT_EQUAL:
        return a != b;
      case LESS_THAN:
        return a < b;
      case GREATER_THAN:
        return a > b;
      case LESS_THAN_OR_EQUAL:
        return a <= b;
      case GREATER_THAN_OR_EQUAL:
        return a >= b;
      default:
        throw new IllegalArgumentException("Invalid operand type: " + operand);
    }
  }

  private String getOperatorString() {
    switch (operand) {
      case EQUAL:
        return EQUAL_CHAR;
      case NOT_EQUAL:
        return NOT_EQUAL_CHAR;
      case LESS_THAN:
        return LESS_THAN_CHAR;
      case GREATER_THAN:
        return GREATER_THAN_CHAR;
      case LESS_THAN_OR_EQUAL:
        return LESS_THAN_OR_EQUAL_CHAR;
      case GREATER_THAN_OR_EQUAL:
        return GREATER_THAN_OR_EQUAL_CHAR;
      default:
        return INVALID_OPERATOR_CHAR;
    }
  }

  @Override
  protected String internalPrettyPrint(Memory mem, Registry reg, int memIdx) {
    int value = mem.getValueAt(memIdx + 1);
    int op1 = value >> 4;
    int op2 = value & 0xF;

    return String.format(
        // e.g. CJP (R0<R1 -> *RES)
        "(%s%s%s %s *%s)",
        Registry.idxToName(op1),
        getOperatorString(),
        Registry.idxToName(op2),
        RIGHT_ARROW_CHAR,
        Registry.REG_RES);
  }

  @Override
  public int[] getAffectedMemoryCells(Memory mem, Registry reg, int memIdx) {
    int dst = reg.getRegister(Registry.REG_RES);
    if (mem.size() <= memIdx + 1) {
      return new int[] {memIdx, dst};
    }
    return new int[] {memIdx, memIdx + 1, dst};
  }

  @Override
  public int[] getAffectedRegisters(Memory mem, Registry reg, int memIdx) {
    int value = mem.getValueAt(memIdx + 1);
    int op1 = value >> 4;
    int op2 = value & 0xF;

    if (op1 > 0 && op1 < Registry.NUM_REGISTERS && op2 > 0 && op2 < Registry.NUM_REGISTERS) {
      return new int[] {op1, op2};
    }
    if (op1 > 0 && op1 < Registry.NUM_REGISTERS) {
      return new int[] {op1};
    }
    if (op2 > 0 && op2 < Registry.NUM_REGISTERS) {
      return new int[] {op2};
    }
    return new int[0];
  }
}
