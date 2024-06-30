package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class LdA extends Instruction {

  public LdA(int operand) {
    super(InstructionFactory.INST_NAME_LDA, operand);
  }

  @Override
  protected void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
    // Operand is unused.
    // Read next value and split into two 4-bit parts, src and dst.
    // src is register index holding memory source address, dst is the destination register index.
    int addresses = mem.getValueAt(pc.next());
    int src = (addresses >> 4) & 0xF;
    int dst = addresses & 0xF;

    int memoryAddress = reg.getValueAt(src);

    reg.setValueAt(dst, mem.getValueAt(memoryAddress));
  }

  @Override
  protected String internalPrettyPrint(Memory mem, Registry reg, int memIdx) {
    if (memIdx >= mem.size()) {
      return "(" + Instruction.INVALID_REG_CHAR + ")";
    }
    int addresses = mem.getValueAt(memIdx + 1);
    int src = (addresses >> 4) & 0xF;
    int dst = addresses & 0xF;
    return String.format(
        // e.g. LDA (*RES -> R0)
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
      return new int[0];
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
