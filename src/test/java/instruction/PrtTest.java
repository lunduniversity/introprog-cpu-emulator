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

public class PrtTest {
  @Test
  public void testPrintOperation() {
    ProgramCounter mockPC = mock(ProgramCounter.class);
    Registry mockRegistry = mock(Registry.class);
    IO mockIO = mock(IO.class);

    int character = (int) 'M'; // Character to be printed

    // Assuming that value have been loaded into register PRT
    when(mockRegistry.getRegister("PRT")).thenReturn(character);

    Prt printInstruction = new Prt(0);
    printInstruction.execute(null, mockRegistry, mockPC, mockIO);

    verify(mockRegistry).getRegister("PRT");
    verify(mockPC, times(2)).next();
    verify(mockIO).print(character);
  }

  @Test
  public void testToString() {
    Prt printInstruction = new Prt(0);
    assertEquals(InstructionFactory.INST_NAME_PRT, printInstruction.toString());
  }
}
