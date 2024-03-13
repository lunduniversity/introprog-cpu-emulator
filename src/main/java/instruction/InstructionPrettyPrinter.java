package instruction;

import model.Registry;

public class InstructionPrettyPrinter {

  public static String prettyPrint(int instruction) {
    int opcode = instruction & 0xF0; // Pick the 4 bits from the 5th to 8th position
    int operand = instruction & 0xF; // Pick the last 4 bits
    switch (opcode) {
      case InstructionFactory.INST_ADD:
        return String.format("%s", InstructionFactory.INST_NAME_ADD);
      case InstructionFactory.INST_SUB:
        return String.format("%s", InstructionFactory.INST_NAME_SUB);
      case InstructionFactory.INST_CPY:
        return String.format(
            "%s (%s | %s)",
            InstructionFactory.INST_NAME_CPY, parseAddrMode(operand >> 2), parseAddrMode(operand));
      case InstructionFactory.INST_MOV:
        return String.format(
            "%s (%s | %s)",
            InstructionFactory.INST_NAME_MOV, parseAddrMode(operand >> 2), parseAddrMode(operand));
      case InstructionFactory.INST__LD:
        return String.format(
            "%s (dst: %s)", InstructionFactory.INST_NAME__LD, Registry.idxToName(operand));
      case InstructionFactory.INST_LDA:
        return String.format(
            "%s (dst: %s)", InstructionFactory.INST_NAME_LDA, Registry.idxToName(operand));
      case InstructionFactory.INST__ST:
        return String.format(
            "%s (src: %s)", InstructionFactory.INST_NAME__ST, Registry.idxToName(operand));
      case InstructionFactory.INST_JMP:
        return String.format(
            "%s (dst: %s)", InstructionFactory.INST_NAME_JMP, Registry.idxToName(operand));
      case InstructionFactory.INST__JE:
        return String.format(
            "%s (dst: %s)", InstructionFactory.INST_NAME__JE, Registry.idxToName(operand));
      case InstructionFactory.INST_JNE:
        return String.format(
            "%s (dst: %s)", InstructionFactory.INST_NAME_JNE, Registry.idxToName(operand));
      case InstructionFactory.INST_HLT:
        return String.format("%s", InstructionFactory.INST_NAME_HLT);
      default:
        return "--";
    }
  }

  /**
   * Parse the addressing mode of an operand. The mode is a 2-bit value, and must occupy the last
   * two bits of the operand. 00 = VAL (constant), 01 = REG (register), 10 = MEM (memory), 11 = I/O
   * (I/O port).
   *
   * @param operand
   * @return
   */
  private static String parseAddrMode(int operand) {
    int mode = operand & 0x3; // Pick the last 2 bits
    switch (mode) {
      case 0:
        return "VAL";
      case 1:
        return "REG";
      case 2:
        return "MEM";
      case 3:
        return "I/O";
      default:
        return "\u2013";
    }
  }
}
