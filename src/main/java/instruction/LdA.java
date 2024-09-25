package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

/**
 * Load Address instruction. Similar to Load, but after reading the next memory value, the value is
 * split into two 4-bit values, each indexing a register. The leading 4 bits determine the source
 * register and the trailing 4 bits determine the destination register. The value in the source
 * register is interpreted as a memory address, and the value at that memory address is stored in
 * the destination register. The operand is not used.
 */
public class LdA extends Instruction {

  public LdA(int operand) {
    super(InstructionFactory.INST_NAME_LDA, operand);
  }

  @Override
  protected void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    // Read the next memory value, and split into two 4-bit parts.
    int addresses = mem.getValueAt(pc.next());
    int src = (addresses >> 4) & 0xF;
    int dst = addresses & 0xF;

    int srcAddress = reg.getValueAt(src);
    int value = mem.getValueAt(srcAddress);

    // Read the value at the memory address, and store in the destination register.
    reg.setValueAt(dst, value);
  }

  @Override
  protected String internalPrettyPrint(Memory mem, Registry reg, int memIdx) {

    if (memIdx >= mem.size()) {
      return Instruction.INVALID_REG_CHAR;
    }
    // Read the next memory value, and split into two 4-bit parts.
    int addresses = mem.getValueAt(memIdx + 1);
    int src = (addresses >> 4) & 0xF;
    int dst = addresses & 0xF;

    return String.format(
        "(*%s %s %s)",
        Registry.idxToName(src), Instruction.RIGHT_ARROW_CHAR, Registry.idxToName(dst));
  }

  @Override
  public int[] getAffectedMemoryCells(Memory mem, Registry reg, int memIdx) {
    if (memIdx >= mem.size()) {
      return new int[] {memIdx};
    }
    int addresses = mem.getValueAt(memIdx + 1);
    int src = (addresses >> 4) & 0xF;

    int srcAddress = reg.getValueAt(src);

    return new int[] {memIdx, memIdx + 1, srcAddress};
  }

  @Override
  public int[] getAffectedRegisters(Memory mem, Registry reg, int memIdx) {
    if (memIdx >= mem.size()) {
      return new int[] {memIdx};
    }
    int addresses = mem.getValueAt(memIdx + 1);
    int src = (addresses >> 4) & 0xF;
    int dst = addresses & 0xF;

    if (src >= 0 && src < Registry.NUM_REGISTERS && dst >= 0 && dst < Registry.NUM_REGISTERS) {
      return new int[] {src, dst};
    }
    if (src >= 0 && src < Registry.NUM_REGISTERS) {
      return new int[] {src};
    }
    if (dst >= 0 && dst < Registry.NUM_REGISTERS) {
      return new int[] {dst};
    }
    return new int[0];
  }
}
