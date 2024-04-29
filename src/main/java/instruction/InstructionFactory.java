package instruction;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;

public class InstructionFactory {
  public static final int INST_NOP = 000; // No-op
  public static final int INST_ADD = 0x10; // Addition
  public static final int INST_SUB = 0x20; // Subtraction
  public static final int INST_CPY = 0x30; // Copy value from one cell to another
  public static final int INST_MOV = 0x40; // Move value from one cell to another
  public static final int INST_LOD = 0x50; // Load the next value from memory into registry
  public static final int INST_LDA = 0x60; // Load address (resolve first) into registry
  public static final int INST_STO = 0x70; // Store register value in memory
  public static final int INST_JMP = 0x80; // Jump to address
  public static final int INST_JEQ = 0x90; // Jump if equal
  public static final int INST_JNE = 0xA0; // Jump if not equal
  public static final int INST_PRT = 0xB0; // Print
  public static final int INST_PRD = 0xC0; // Print
  public static final int INST_PRL = 0xD0; // Print Loop
  public static final int INST_HLT = 0xE0; // Halt

  // PRT
  // Add 2 columns: Instr, Char

  public static final String INST_NAME_NOP = "NOP";
  public static final String INST_NAME_ADD = "ADD";
  public static final String INST_NAME_SUB = "SUB";
  public static final String INST_NAME_CPY = "CPY";
  public static final String INST_NAME_MOV = "MOV";
  public static final String INST_NAME_LOD = "LD";
  public static final String INST_NAME_LDA = "LDA";
  public static final String INST_NAME_STO = "ST";
  public static final String INST_NAME_JMP = "JMP";
  public static final String INST_NAME_JEQ = "JEQ";
  public static final String INST_NAME_JNE = "JNE";
  public static final String INST_NAME_PRT = "PRT";
  public static final String INST_NAME_PRD = "PRD";
  public static final String INST_NAME_PRL = "PRL";
  public static final String INST_NAME_HLT = "HLT";

  public boolean isInstruction(int code) {
    return code == INST_NOP
        || code == INST_ADD
        || code == INST_SUB
        || code == INST_CPY
        || code == INST_MOV
        || code == INST_LOD
        || code == INST_LDA
        || code == INST_STO
        || code == INST_JMP
        || code == INST_JEQ
        || code == INST_JNE
        || code == INST_PRT
        || code == INST_PRD
        || code == INST_PRL
        || code == INST_HLT;
  }

  public Instruction createInstruction(int code) {
    // Extract the opcode (first 4 bits) and the operand (last 4 bits)
    int opcode = code & 0xF0;
    int operand = code & 0x0F;
    switch (opcode) {
      case INST_NOP:
        return new Nop(operand);
      case INST_ADD:
        return new Add(operand);
      case INST_SUB:
        return new Sub(operand);
      case INST_CPY:
        return new Cpy(operand);
      case INST_MOV:
        return new Mov(operand);
      case INST_LOD:
        return new Ld(operand);
      case INST_LDA:
        return new LdA(operand);
      case INST_STO:
        return new St(operand);
      case INST_JMP:
        return new Jmp(operand);
      case INST_JEQ:
        return new Je(operand);
      case INST_JNE:
        return new Jne(operand);
      case INST_PRT:
        return new PrT(operand);
      case INST_PRD:
        return new PrD(operand);
      case INST_PRL:
        return new PrL(operand);
      case INST_HLT:
        return new Hlt(operand);
      default:
        return new NullInstruction(opcode);
    }
  }

  private static class NullInstruction extends Instruction {

    private int opcode;

    public NullInstruction(int opcode) {
      super("--", 0);
      this.opcode = opcode >> 4;
    }

    @Override
    protected void internalExecute(Memory mem, Registry reg, ProgramCounter pc, IO io) {
      throw new UnsupportedOperationException(
          String.format("Unknown instruction: %s", toBinaryString(opcode, 4)));
    }

    @Override
    protected String printOperand() {
      return "";
    }

    @Override
    public int[] getAffectedMemoryCells(Memory mem, Registry reg, ProgramCounter pc) {
      return new int[] {pc.getCurrentIndex()};
    }

    @Override
    public int[] getAffectedRegisters(Memory mem, Registry reg, ProgramCounter pc) {
      return new int[0];
    }
  }
}
