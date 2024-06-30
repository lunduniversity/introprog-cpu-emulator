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

class PrTTest {
  @Test
  void testPrintOperation() {
    ProgramCounter mockPC = mock(ProgramCounter.class);
    Registry mockRegistry = mock(Registry.class);
    IO mockIO = mock(IO.class);

    char character = 'M'; // Character to be printed
    int charCode = (int) character;

    // Assuming that value have been loaded into register PRT
    when(mockRegistry.getRegister(Registry.REG_OUT)).thenReturn(charCode);

    PrT printInstruction = new PrT(0);
    printInstruction.execute(null, mockRegistry, mockPC, mockIO);

    verify(mockRegistry).getRegister(Registry.REG_OUT);
    verify(mockPC, times(1)).next();
    verify(mockIO).print(character);
  }

  @Test
  void testPrettyPrint() {
    PrT printInstruction = new PrT(0);
    assertEquals(InstructionFactory.INST_NAME_PRT, printInstruction.prettyPrint(null, null, 0));
  }
}
