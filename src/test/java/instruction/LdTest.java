package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import model.Memory;
import model.ProgramCounter;
import model.Registry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LdTest {

  private Memory mockMemory;
  private Registry mockRegistry;
  private ProgramCounter mockPC;

  @BeforeEach
  void setup() {
    mockMemory = mock(Memory.class);
    mockRegistry = mock(Registry.class);
    mockPC = mock(ProgramCounter.class);
    when(mockMemory.size()).thenReturn(10);
  }

  @Test
  void testLoadOperation() {
    // Setup the operand, memory value, and the address
    int operand = 0; // The register to load the value into
    int memoryValue = 123; // The value to be loaded from memory
    int memoryAddress = 1; // The next address in the program counter

    // Simulate the program counter pointing to the next memory address
    when(mockPC.next()).thenReturn(memoryAddress);

    // Simulate memory returning a specific value at the given address
    when(mockMemory.getValueAt(memoryAddress)).thenReturn(memoryValue);

    Ld ldInstruction = new Ld(operand);
    ldInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify the registry's setRegister method is called with the correct arguments
    verify(mockRegistry).setValueAt(operand, memoryValue);
  }

  @Test
  void testPrettyPrint() {
    when(mockMemory.getValueAt(1)).thenReturn(17);
    when(mockMemory.getValueAt(3)).thenReturn(3);
    when(mockMemory.getValueAt(5)).thenReturn(64);
    Ld loadOP1 = new Ld(Registry.nameToIdx(Registry.REG_OP1));
    Ld loadOP2 = new Ld(Registry.nameToIdx(Registry.REG_OP2));
    Ld loadR2 = new Ld(Registry.nameToIdx(Registry.REG_R2));
    assertEquals(
        InstructionFactory.INST_NAME_LOD + " (17 " + Instruction.RIGHT_ARROW_CHAR + " OP1)",
        loadOP1.prettyPrint(mockMemory, mockRegistry, 0));
    assertEquals(
        InstructionFactory.INST_NAME_LOD + " (3 " + Instruction.RIGHT_ARROW_CHAR + " OP2)",
        loadOP2.prettyPrint(mockMemory, mockRegistry, 2));
    assertEquals(
        InstructionFactory.INST_NAME_LOD + " (64 " + Instruction.RIGHT_ARROW_CHAR + " R2)",
        loadR2.prettyPrint(mockMemory, mockRegistry, 4));
  }
}
