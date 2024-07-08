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

class LdATest {

  private Memory mockMemory;
  private Registry mockRegistry;
  private ProgramCounter mockPC;

  @BeforeEach
  void setup() {
    mockMemory = mock(Memory.class);
    mockRegistry = mock(Registry.class);
    mockPC = mock(ProgramCounter.class);
    when(mockMemory.size()).thenReturn(50);
  }

  @Test
  void testLoadFromAddressOperation() {
    int operand = Registry.nameToIdx(Registry.REG_R1); // Operand: register to load value into
    int value = 123; // The value to be loaded from memory
    int memoryAddress = 33; // The address in memory where value is stored

    // Simulate the program counter pointing to the next memory address
    when(mockPC.next()).thenReturn(1);

    // Simulate memory returning the memory address
    when(mockMemory.getValueAt(1)).thenReturn(memoryAddress);

    // Simulate memory returning the value from the memory address
    when(mockMemory.getValueAt(memoryAddress)).thenReturn(value);

    LdA ldaInstruction = new LdA(operand);
    ldaInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify the registry's setRegister method is called with the correct value
    verify(mockRegistry).setValueAt(operand, value);
  }

  @Test
  void testPrettyPrint() {
    when(mockMemory.getValueAt(1)).thenReturn(81);

    LdA load = new LdA(Registry.nameToIdx(Registry.REG_R1));
    assertEquals(
        InstructionFactory.INST_NAME_LDA + " (m[81] " + Instruction.RIGHT_ARROW_CHAR + " R1)",
        load.prettyPrint(mockMemory, mockRegistry, 0));
  }
}
