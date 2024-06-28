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
  void testEvaluate() {
    Ld loadOP1 = new Ld(Registry.nameToIdx(Registry.REG_OP1));
    Ld loadOP2 = new Ld(Registry.nameToIdx(Registry.REG_OP2));
    Ld loadR2 = new Ld(Registry.nameToIdx(Registry.REG_R1));
    assertEquals(
        InstructionFactory.INST_NAME_LOD + " (dst: OP1)",
        loadOP1.prettyPrint(mockMemory, mockRegistry, 0));
    assertEquals(
        InstructionFactory.INST_NAME_LOD + " (dst: OP2)",
        loadOP2.prettyPrint(mockMemory, mockRegistry, 0));
    assertEquals(
        InstructionFactory.INST_NAME_LOD + " (dst: R2)",
        loadR2.prettyPrint(mockMemory, mockRegistry, 0));
  }
}
