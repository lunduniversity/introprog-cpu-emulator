package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InstructionFactoryTest {

  private InstructionFactory factory;

  @BeforeEach
  public void setUp() {
    // Create a new instance of InstructionFactory
    factory = new InstructionFactory();
  }

  @Test
  public void testIsInstructionWithValidCodes() {
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_ADD),
        "INST_ADD should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_SUB),
        "INST_SUB should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_MOV),
        "INST_MOV should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST__LD),
        "INST__LD should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST__ST),
        "INST__ST should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_JMP),
        "INST_JMP should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST__JE),
        "INST__JE should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_JNE),
        "INST_JNE should be recognized as an instruction.");
    assertTrue(
        factory.isInstruction(InstructionFactory.INST_HLT),
        "INST_HLT should be recognized as an instruction.");
  }

  @Test
  public void testIsInstructionWithInvalidCode() {
    int invalidCode = 0x0F; // Assuming 0x0F is not defined as an instruction
    assertFalse(
        factory.isInstruction(invalidCode),
        "Invalid code should not be recognized as an instruction.");
  }

  @Test
  public void testCreateInstructionForAllValidCodes() {
    // Test each instruction creation
    assertInstanceOf(
        Add.class,
        factory.createInstruction(InstructionFactory.INST_ADD),
        "Should create an instance of Add.");
    assertInstanceOf(
        Sub.class,
        factory.createInstruction(InstructionFactory.INST_SUB),
        "Should create an instance of Sub.");
    assertInstanceOf(
        Mov.class,
        factory.createInstruction(InstructionFactory.INST_MOV),
        "Should create an instance of Mov.");
    assertInstanceOf(
        Ld.class,
        factory.createInstruction(InstructionFactory.INST__LD),
        "Should create an instance of Ld.");
    assertInstanceOf(
        St.class,
        factory.createInstruction(InstructionFactory.INST__ST),
        "Should create an instance of St.");
    assertInstanceOf(
        Jmp.class,
        factory.createInstruction(InstructionFactory.INST_JMP),
        "Should create an instance of Jmp.");
    assertInstanceOf(
        Je.class,
        factory.createInstruction(InstructionFactory.INST__JE),
        "Should create an instance of Je.");
    assertInstanceOf(
        Jne.class,
        factory.createInstruction(InstructionFactory.INST_JNE),
        "Should create an instance of Jne.");
    assertInstanceOf(
        Hlt.class,
        factory.createInstruction(InstructionFactory.INST_HLT),
        "Should create an instance of Hlt.");
  }

  @Test
  public void testCreateInstructionWithInvalidCode() {
    int invalidCode = 0x0F; // Assuming 0x0F is not defined as an instruction
    Exception exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> factory.createInstruction(invalidCode),
            "Unknown instruction code should throw IllegalArgumentException.");
    assertEquals(
        "Unknown instruction: 0x0F",
        exception.getMessage(),
        "Exception message should match for unknown instruction codes.");
  }
}
