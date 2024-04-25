package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.IO;
import model.ProgramCounter;
import model.Registry;
import org.junit.jupiter.api.Test;

class PrDTest {
  @Test
  void testPrintOperation() {
    ProgramCounter mockPC = mock(ProgramCounter.class);
    Registry mockRegistry = mock(Registry.class);
    IO mockIO = mock(IO.class);

    int value = 57; // Value to be printed

    // Assuming that value have been loaded into register PRT
    when(mockRegistry.getRegister(Registry.REG_OUT)).thenReturn(value);

    PrD printInstruction = new PrD(0);
    printInstruction.execute(null, mockRegistry, mockPC, mockIO);

    verify(mockRegistry).getRegister(Registry.REG_OUT);
    verify(mockPC, times(1)).next();
    verify(mockIO).print(value);
  }

  @Test
  void testToString() {
    PrD printInstruction = new PrD(0);
    assertEquals(InstructionFactory.INST_NAME_PRD, printInstruction.toString());
  }
}
