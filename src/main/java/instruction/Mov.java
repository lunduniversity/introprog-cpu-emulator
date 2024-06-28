package instruction;

import io.IO;
import java.util.ArrayList;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class Mov extends Instruction {

  // Move does the same as Cope, but resets the source to 0 after reading it.
  public Mov(int operand) {
    super(InstructionFactory.INST_NAME_MOV, operand);
  }

  @Override
  protected void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    // operand is the 4 right-most bits of the instruction. Out of these 4 bits, use bits 1 and 2
    // (from the left) for the source, and bits 3 and 4 for the destination, according to this rule:
    // 00: constant value (only for source, not for destination)
    // 01: register address/index
    // 10: memory address
    int srcType = (operand >> 2) & 0x3;
    int destType = (operand) & 0x3;

    // check for errors first
    if (srcType == ADDR_TYPE_INVALID) {
      throw new IllegalArgumentException(
          String.format("Invalid source type: %s", toBinaryString(srcType, 2)));
    }
    if (destType == ADDR_TYPE_INVALID || destType == ADDR_TYPE_CONSTANT) {
      throw new IllegalArgumentException(
          String.format("Invalid destination type: %s", toBinaryString(destType, 2)));
    }

    int value = getSrcValue(srcType, mem, reg, pc);
    int dst = mem.getValueAt(pc.next());

    if (destType == ADDR_TYPE_MEMORY) {
      mem.setValueAt(dst, value);
    } else {
      reg.setValueAt(dst, value);
    }
  }

  private int getSrcValue(int srcType, Memory mem, Registry reg, ProgramCounter pc) {
    int srcIdx = pc.next();
    int src = mem.getValueAt(srcIdx);
    if (srcType == ADDR_TYPE_REGISTER) {
      int value = reg.getValueAt(src);
      reg.setValueAt(src, 0);
      return value;
    } else if (srcType == ADDR_TYPE_MEMORY) {
      int value = mem.getValueAt(src);
      mem.setValueAt(src, 0);
      return value;
    }
    mem.setValueAt(srcIdx, 0);
    return src;
  }

  @Override
  protected String internalPrettyPrint(Memory mem, Registry reg, int memIdx) {
    return String.format("(%s | %s)", parseAddrMode(operand >> 2), parseAddrMode(operand));
  }

  @Override
  public int[] getAffectedMemoryCells(Memory mem, Registry reg, int memIdx) {
    int srcType = (operand >> 2) & 0x3;
    int destType = (operand) & 0x3;

    ArrayList<Integer> indices = new ArrayList<>();
    indices.add(memIdx);
    indices.add(memIdx + 1);
    indices.add(memIdx + 2);

    if (srcType == ADDR_TYPE_MEMORY) {
      indices.add(mem.getValueAt(memIdx + 1));
    }
    if (destType == ADDR_TYPE_MEMORY) {
      indices.add(mem.getValueAt(memIdx + 2));
    }

    return indices.stream().mapToInt(i -> i).toArray();
  }

  @Override
  public int[] getAffectedRegisters(Memory mem, Registry reg, int memIdx) {
    int srcType = (operand >> 2) & 0x3;
    int destType = (operand) & 0x3;

    ArrayList<Integer> indices = new ArrayList<>();
    if (srcType == ADDR_TYPE_REGISTER) {
      int regIdx = mem.getValueAt(memIdx + 1);
      if (Registry.isValidIndex(regIdx)) {
        indices.add(regIdx);
      }
    }
    if (destType == ADDR_TYPE_REGISTER) {
      int regIdx = mem.getValueAt(memIdx + 2);
      if (Registry.isValidIndex(regIdx)) {
        indices.add(regIdx);
      }
    }
    return indices.stream().mapToInt(i -> i).toArray();
  }
}
