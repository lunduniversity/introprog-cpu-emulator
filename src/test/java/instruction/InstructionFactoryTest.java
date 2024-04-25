package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import model.ProgramCounter;
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
        factory.isInstruction(InstructionFactory.INST_CPY),
        "INST_CPY should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_MOV),
        "INST_MOV should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_LOD),
        "INST__LD should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_LDA),
        "INST_LDA should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_STO),
        "INST__ST should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_JMP),
        "INST_JMP should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_JEQ),
        "INST__JE should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_JNE),
        "INST_JNE should be recognized as an instruction.");
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
    int invalidCode = 0xF0; // Assuming 0xF0 is not defined as an instruction
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
        Cpy.class,
        factory.createInstruction(InstructionFactory.INST_CPY),
        "Should create an instance of Cpy.");
    assertInstanceOf(
        Mov.class,
        factory.createInstruction(InstructionFactory.INST_MOV),
        "Should create an instance of Mov.");
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
        Jmp.class,
        factory.createInstruction(InstructionFactory.INST_JMP),
        "Should create an instance of Jmp.");
    assertInstanceOf(
        Je.class,
        factory.createInstruction(InstructionFactory.INST_JEQ),
        "Should create an instance of Je.");
    assertInstanceOf(
        Jne.class,
        factory.createInstruction(InstructionFactory.INST_JNE),
        "Should create an instance of Jne.");
    assertInstanceOf(
        PrT.class,
        factory.createInstruction(InstructionFactory.INST_PRT),
        "Should create an instance of Hlt.");
    assertInstanceOf(
        PrL.class,
        factory.createInstruction(InstructionFactory.INST_PRL),
        "Should create an instance of Hlt.");
    assertInstanceOf(
        Hlt.class,
        factory.createInstruction(InstructionFactory.INST_HLT),
        "Should create an instance of Hlt.");
  }

  @Test
  void testCreateInstructionWithInvalidCode() {
    // Factory should return a null-object for invalid codes.
    // The null objects should be printable but have no functionality.
    int invalidCode = 0xF0; // Assuming 0xF0 is not defined as an instruction
    Instruction invalidInstruction = factory.createInstruction(invalidCode);
    assertEquals("--", invalidInstruction.toString(), "Invalid instruction should be printable.");

    // If executed, null-object should increment the PC, but throw an exception.
    ProgramCounter mockPC = mock(ProgramCounter.class);
    assertThrows(
        UnsupportedOperationException.class,
        () -> invalidInstruction.execute(null, null, mockPC, null));
  }
}
