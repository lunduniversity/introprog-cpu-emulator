package instruction;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import model.Memory;
import model.ProgramCounter;
import model.Registry;
import org.junit.jupiter.api.Test;

public class LdTest {
  @Test
  public void testLoadOperation() {
    Memory mockMemory = mock(Memory.class);
    Registry mockRegistry = mock(Registry.class);
    ProgramCounter mockPC = mock(ProgramCounter.class);

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
    verify(mockRegistry).setRegister(operand, memoryValue);
  }
}
