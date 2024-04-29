package instruction;

import io.IO;
import java.util.ArrayList;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class Cpy extends Instruction {

  public Cpy(int operand) {
    super(InstructionFactory.INST_NAME_CPY, operand);
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
    if (srcType != 0b00 && srcType != 0b01 && srcType != 0b10) {
      throw new IllegalArgumentException(
          String.format("Invalid source type: %s", toBinaryString(srcType, 2)));
    }
    if (destType != 0b10 && destType != 0b01) {
      throw new IllegalArgumentException(
          String.format("Invalid destination type: %s", toBinaryString(destType, 2)));
    }

    int src = mem.getValueAt(pc.next());
    int dst = mem.getValueAt(pc.next());
    int value = getSrcValue(srcType, src, mem, reg);

    if (destType == 0b10) {
      mem.setValueAt(dst, value);
    } else {
      reg.setValueAt(dst, value);
    }
  }

  private int getSrcType(int operand) {
    return (operand >> 2) & 0x3;
  }

  private int getSrcValue(int srcType, int src, Memory mem, Registry reg) {
    if (srcType == 0b01) {
      return reg.getValueAt(src);
    } else if (srcType == 0b10) {
      return mem.getValueAt(src);
    }
    return src;
  }

  @Override
  protected String printOperand() {
    return String.format("(%s | %s)", parseAddrMode(operand >> 2), parseAddrMode(operand));
  }

  @Override
  public int[] getAffectedMemoryCells(Memory mem, Registry reg, ProgramCounter pc) {
    int cur = pc.getCurrentIndex();
    int srcType = (operand >> 2) & 0x3;
    int destType = (operand) & 0x3;

    ArrayList<Integer> indices = new ArrayList<>();
    indices.add(cur);
    indices.add(cur + 1);
    indices.add(cur + 2);

    if (srcType == 0b10) {
      indices.add(mem.getValueAt(cur + 1));
    }
    if (destType == 0b10) {
      indices.add(mem.getValueAt(cur + 2));
    }

    return indices.stream().mapToInt(i -> i).toArray();
  }

  @Override
  public int[] getAffectedRegisters(Memory mem, Registry reg, ProgramCounter pc) {
    int cur = pc.getCurrentIndex();
    int srcType = (operand >> 2) & 0x3;
    int destType = (operand) & 0x3;

    ArrayList<Integer> indices = new ArrayList<>();
    if (srcType == 0b01) {
      int regIdx = mem.getValueAt(cur + 1);
      if (Registry.isValidIndex(regIdx)) {
        indices.add(regIdx);
      }
    }
    if (destType == 0b01) {
      int regIdx = mem.getValueAt(cur + 2);
      if (Registry.isValidIndex(regIdx)) {
        indices.add(regIdx);
      }
    }
    return indices.stream().mapToInt(i -> i).toArray();
  }
}
