package instruction;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import model.Memory;
import model.ProgramCounter;
import model.Registry;
import org.junit.jupiter.api.Test;

public class StTest {
  @Test
  public void testStoreOperation() {
    Memory mockMemory = mock(Memory.class);
    Registry mockRegistry = mock(Registry.class);
    ProgramCounter mockPC = mock(ProgramCounter.class);

    // Setup the operand (source register), the value in that register, and the destination address
    // in memory
    int operand = 0; // The register from which the value is to be stored
    int registerValue = 123; // The value stored in the source register
    int memoryAddress = 200; // The destination address in memory where the value should be stored
    int nextMemoryLocation = 1; // The next memory location according to the program counter

    // Simulate fetching the value from the register specified by the operand
    when(mockRegistry.getRegister(operand)).thenReturn(registerValue);

    // Simulate the program counter pointing to the next memory address that contains the
    // destination address
    when(mockPC.next()).thenReturn(nextMemoryLocation);

    // Simulate fetching the destination address from memory
    when(mockMemory.getValueAt(nextMemoryLocation)).thenReturn(memoryAddress);

    St stInstruction = new St(operand);
    stInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify memory's setValueAt method is called with the correct destination address and value
    verify(mockMemory).setValueAt(memoryAddress, registerValue);
  }
}