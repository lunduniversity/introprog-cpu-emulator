package instruction;

public class InstructionFactory {
    public static final int INST_ADD = 0x01; // Addition
    public static final int INST_SUB = 0x02; // Subtraction
    public static final int INST_MOV = 0x03; // Copy value from one cell to another
    public static final int INST__LD = 0x04; // Load into register
    public static final int INST__ST = 0x05; // Store register value in memory
    public static final int INST_JMP = 0x06; // Jump to address
    public static final int INST__JE = 0x07; // Jump if equal
    public static final int INST_JNE = 0x08; // Jump if not equal
    public static final int INST_HLT = 0x09; // Halt

    public static final String INST_NAME_ADD = "ADD";
    public static final String INST_NAME_SUB = "SUB";
    public static final String INST_NAME_MOV = "MOV";
    public static final String INST_NAME__LD = "LD";
    public static final String INST_NAME__ST = "ST";
    public static final String INST_NAME_JMP = "JMP";
    public static final String INST_NAME__JE = "JE";
    public static final String INST_NAME_JNE = "JNE";
    public static final String INST_NAME_HLT = "HLT";

    public static boolean isInstruction(int code) {
        return code == INST_ADD ||
                code == INST_SUB ||
                code == INST_MOV ||
                code == INST__LD ||
                code == INST__ST ||
                code == INST_JMP ||
                code == INST__JE ||
                code == INST_JNE ||
                code == INST_HLT;
    }

    public static Instruction createInstruction(int code) {
        switch (code) {
            case INST_ADD:
                return new Add();
            case INST_SUB:
                return new Sub();
            case INST_MOV:
                return new Mov();
            case INST__LD:
                return new Ld();
            case INST__ST:
                return new St();
            case INST_JMP:
                return new Jmp();
            case INST__JE:
                return new Je();
            case INST_JNE:
                return new Jne();
            case INST_HLT:
                return new Hlt();
            default:
                throw new IllegalArgumentException(String.format("Unknown instruction: 0x%02X", code));
        }
    }

}
