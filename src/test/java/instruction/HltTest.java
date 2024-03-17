package instruction;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import model.ProgramCounter;
import org.junit.jupiter.api.Test;

public class HltTest {
  @Test
  public void testHltOperationHaltsProgramCounter() {
    // Mock the ProgramCounter
    ProgramCounter mockPC = mock(ProgramCounter.class);

    // Create the Hlt instruction
    Hlt hltInstruction = new Hlt(0);

    // Execute the instruction with the mocked ProgramCounter
    hltInstruction._execute(null, null, mockPC, null);

    // Verify that pc.halt() was called exactly once
    verify(mockPC, times(1)).halt();
  }
}
