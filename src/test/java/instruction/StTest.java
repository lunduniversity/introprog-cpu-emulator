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

class StTest {

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
  void testStoreOperation() {

    // Setup source and destination registers
    int operand = Registry.nameToIdx(Registry.REG_RES);
    int registerValue = 35; // The value stored in the source register
    int memoryAddress = 8; // The destination address in memory where the value should be stored
    int nextMemoryLocation = 1; // The next memory location according to the program counter

    // Simulate the program counter pointing to the next memory address that contains the
    // destination address
    when(mockPC.next()).thenReturn(nextMemoryLocation);

    // Simulate fetching the destination address from the memory
    when(mockMemory.getValueAt(nextMemoryLocation)).thenReturn(memoryAddress);

    // Simulate fetching the value from the register specified by the operand
    when(mockRegistry.getValueAt(operand)).thenReturn(registerValue);

    St stInstruction = new St(operand);
    stInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify memory's setValueAt method is called with the correct destination address and value
    verify(mockMemory).getValueAt(nextMemoryLocation);
    verify(mockMemory).setValueAt(memoryAddress, registerValue);
  }

  @Test
  void testPrettyPrint() {
    // Setup source and destination registers
    int operand = Registry.nameToIdx(Registry.REG_RES);
    int memoryAddress = 8; // The destination address in memory where the value should be stored

    when(mockMemory.getValueAt(1)).thenReturn(memoryAddress);

    St store = new St(operand);
    assertEquals(
        InstructionFactory.INST_NAME_STO + " (RES " + Instruction.RIGHT_ARROW_CHAR + " m[8])",
        store.prettyPrint(mockMemory, mockRegistry, 0));
  }
}
