package instruction;

public class InstructionFactory {
  public static final int INST_ADD = 0x10; // Addition
  public static final int INST_SUB = 0x20; // Subtraction
  public static final int INST_CPY = 0x30; // Copy value from one cell to another
  public static final int INST_MOV = 0x40; // Move value from one cell to another
  public static final int INST__LD = 0x50; // Load the next value from memory into registry
  public static final int INST_LDA = 0x60; // Load address (resolve first) into registry
  public static final int INST__ST = 0x70; // Store register value in memory
  public static final int INST_JMP = 0x80; // Jump to address
  public static final int INST__JE = 0x90; // Jump if equal
  public static final int INST_JNE = 0xA0; // Jump if not equal
  public static final int INST_HLT = 0xB0; // Halt

  // PRT
  // Add 2 columns: Instr, Char

  public static final String INST_NAME_ADD = "ADD";
  public static final String INST_NAME_SUB = "SUB";
  public static final String INST_NAME_CPY = "CPY";
  public static final String INST_NAME_MOV = "MOV";
  public static final String INST_NAME__LD = "LD";
  public static final String INST_NAME_LDA = "LDA";
  public static final String INST_NAME__ST = "ST";
  public static final String INST_NAME_JMP = "JMP";
  public static final String INST_NAME__JE = "JE";
  public static final String INST_NAME_JNE = "JNE";
  public static final String INST_NAME_HLT = "HLT";

  public boolean isInstruction(int code) {
    return code == INST_ADD
        || code == INST_SUB
        || code == INST_CPY
        || code == INST_MOV
        || code == INST__LD
        || code == INST_LDA
        || code == INST__ST
        || code == INST_JMP
        || code == INST__JE
        || code == INST_JNE
        || code == INST_HLT;
  }

  public Instruction createInstruction(int code) {
    // Extract the opcode (first 4 bits) and the operand (last 4 bits)
    int opcode = code & 0xF0;
    int operand = code & 0x0F;
    switch (opcode) {
      case INST_ADD:
        return new Add(operand);
      case INST_SUB:
        return new Sub(operand);
      case INST_CPY:
        return new Cpy(operand);
      case INST_MOV:
        return new Mov(operand);
      case INST__LD:
        return new Ld(operand);
      case INST_LDA:
        return new LdA(operand);
      case INST__ST:
        return new St(operand);
      case INST_JMP:
        return new Jmp(operand);
      case INST__JE:
        return new Je(operand);
      case INST_JNE:
        return new Jne(operand);
      case INST_HLT:
        return new Hlt(operand);
      default:
        throw new IllegalArgumentException(String.format("Unknown instruction: 0x%02X", code));
    }
  }
}
