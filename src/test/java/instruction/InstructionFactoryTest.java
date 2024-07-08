package instruction;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InstructionFactoryTest {

  private InstructionFactory factory;

  @BeforeEach
  void setUp() {
    // Create a new instance of InstructionFactory
    factory = new InstructionFactory();
  }

  @Test
  void testIsInstructionWithValidCodes() {
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_NOP),
        "INST_NOP should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_ADD),
        "INST_ADD should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_SUB),
        "INST_SUB should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_INC),
        "INST_INC should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_CPY),
        "INST_CPY should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_LOD),
        "INST_LOD should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_LDA),
        "INST_LDA should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_STO),
        "INST_STO should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_STA),
        "INST_STA should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_JMP),
        "INST_JMP should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_CJP),
        "INST_CJP should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_PRT),
        "INST_PRT should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_PRL),
        "INST_PRL should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_HLT),
        "INST_HLT should be recognized as an instruction.");
  }

  @Test
  void testIsInstructionWithInvalidCode() {
    int invalidCode = 0x100; // Any value greater than 0xFF is invalid
    assertFalse(
        factory.isInstruction(invalidCode),
        "Invalid code should not be recognized as an instruction.");
  }

  @Test
  void testCreateInstructionForAllValidCodes() {
    // Test each instruction creation
    assertInstanceOf(
        Nop.class,
        factory.createInstruction(InstructionFactory.INST_NOP),
        "Should create an instance of Nop.");
    assertInstanceOf(
        Add.class,
        factory.createInstruction(InstructionFactory.INST_ADD),
        "Should create an instance of Add.");
    assertInstanceOf(
        Sub.class,
        factory.createInstruction(InstructionFactory.INST_SUB),
        "Should create an instance of Sub.");
    assertInstanceOf(
        Inc.class,
        factory.createInstruction(InstructionFactory.INST_INC),
        "Should create an instance of Inc.");
    assertInstanceOf(
        Cpy.class,
        factory.createInstruction(InstructionFactory.INST_CPY),
        "Should create an instance of Cpy.");
    assertInstanceOf(
        Ld.class,
        factory.createInstruction(InstructionFactory.INST_LOD),
        "Should create an instance of Ld.");
    assertInstanceOf(
        LdA.class,
        factory.createInstruction(InstructionFactory.INST_LDA),
        "Should create an instance of Ld.");
    assertInstanceOf(
        St.class,
        factory.createInstruction(InstructionFactory.INST_STO),
        "Should create an instance of St.");
    assertInstanceOf(
        StA.class,
        factory.createInstruction(InstructionFactory.INST_STA),
        "Should create an instance of StA.");
    assertInstanceOf(
        Jmp.class,
        factory.createInstruction(InstructionFactory.INST_JMP),
        "Should create an instance of Jmp.");
    assertInstanceOf(
        CJp.class,
        factory.createInstruction(InstructionFactory.INST_CJP),
        "Should create an instance of CJp.");
    assertInstanceOf(
        PrT.class,
        factory.createInstruction(InstructionFactory.INST_PRT),
        "Should create an instance of Prt.");
    assertInstanceOf(
        PrL.class,
        factory.createInstruction(InstructionFactory.INST_PRL),
        "Should create an instance of Prl.");
    assertInstanceOf(
        Hlt.class,
        factory.createInstruction(InstructionFactory.INST_HLT),
        "Should create an instance of Hlt.");
  }

  @Test
  void testCreateInstructionWithInvalidCode() {
    // Factory should return a null-object for invalid codes.
    // The null objects should be printable but have no functionality.

    // Update: There are now 16 total instructions, which is the maximum number of instructions that
    // can be represented by a 4-bit code. Any value greater than 0xFF is invalid. However, the
    // factory uses bit operations to extract only the opcode and operand, so even if the code is
    // greater than 0xFF, the factory will still return an instruction object.
    // The null object instruction still exists, but can no longer be created by the factory, so
    // long
    // as there are 16 instructions.

    int invalidCode = 0x100; // Invalid 1, followed by 0x00, which is NOP
    Instruction invalidInstruction = factory.createInstruction(invalidCode);
    assertInstanceOf(Nop.class, invalidInstruction, "Should create an instance of Nop.");

    invalidCode = 0x7A5; // Invalid 7, followed by opcode A, which is CJp
    invalidInstruction = factory.createInstruction(invalidCode);
    assertInstanceOf(CJp.class, invalidInstruction, "Should create an instance of CJp.");

    // Old test, that actually tested the null object instruction
    // assertEquals("--", invalidInstruction.toString(), "Invalid instruction should be
    // printable.");

    // // If executed, null-object should increment the PC, but throw an exception.
    // ProgramCounter mockPC = mock(ProgramCounter.class);
    // assertThrows(
    //     UnsupportedOperationException.class,
    //     () -> invalidInstruction.execute(null, null, mockPC, null));
  }
}
