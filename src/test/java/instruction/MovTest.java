package instruction;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import model.Memory;
import model.ProgramCounter;

public class MovTest {

  // @Test
  public void testMovOperation() {
    Memory mockMemory = mock(Memory.class);
    ProgramCounter mockPC = mock(ProgramCounter.class);

    // Setup for reading source and destination addresses
    when(mockPC.next()).thenReturn(0x82);
    int sourceValue = 42;
    when(mockMemory.getValueAt(((int) 1))).thenReturn(sourceValue); // Source value to move

    Mov movInstruction = new Mov(0);
    movInstruction._execute(mockMemory, null, mockPC, null);

    // Verify the source value is copied to the destination address
    verify(mockMemory).setValueAt(eq(((int) 2)), argThat(argument -> argument == sourceValue));
  }
}
