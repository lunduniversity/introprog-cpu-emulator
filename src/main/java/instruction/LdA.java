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
    int addresses = mem.getValueAt(pc.next());
    int src = (addresses >> 4) & 0xF;
    int dst = addresses & 0xF;

    reg.setValueAt(dst, mem.getValueAt(src));
  }

  @Override
  protected String internalPrettyPrint(Memory mem, Registry reg, int memIdx) {
    int addresses = mem.getValueAt(memIdx);
    int src = (addresses >> 4) & 0xF;
    int dst = addresses & 0xF;
    return String.format(
        // e.g. LDA (*RES -> R0)
        "(*%s %s %s)",
        Registry.idxToName(src), Instruction.RIGHT_ARROW_CHAR, Registry.idxToName(dst));
  }

  @Override
  public int[] getAffectedMemoryCells(Memory mem, Registry reg, int memIdx) {
    int addresses = mem.getValueAt(memIdx);
    int src = (addresses >> 4) & 0xF;
    int srcAddress = mem.getValueAt(src);
    return new int[] {memIdx, memIdx + 1, srcAddress};
  }

  @Override
  public int[] getAffectedRegisters(Memory mem, Registry reg, int memIdx) {
    int addresses = mem.getValueAt(memIdx);
    int src = (addresses >> 4) & 0xF;
    int dst = addresses & 0xF;
    return new int[] {src, dst};
  }
}
