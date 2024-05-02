package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.IO;
import model.ProgramCounter;
import org.junit.jupiter.api.Test;

class HltTest {
  @Test
  void testHltOperationHaltsProgramCounter() {
    // Mock the ProgramCounter
    ProgramCounter mockPC = mock(ProgramCounter.class);

    // Mock the IO
    IO mockIO = mock(IO.class);

    // Create the Hlt instruction
    Hlt hltInstruction = new Hlt(0);

    // Execute the instruction with the mocked ProgramCounter
    hltInstruction.execute(null, null, mockPC, mockIO);

    // Verify that pc.halt() was called exactly once
    verify(mockPC, times(1)).halt(ProgramCounter.NORMAL_HALT);
    verify(mockIO, times(1)).print('\n');
  }

  @Test
  void testToString() {
    Hlt hltInstruction = new Hlt(0);
    assertEquals(InstructionFactory.INST_NAME_HLT, hltInstruction.toString());
  }
}
