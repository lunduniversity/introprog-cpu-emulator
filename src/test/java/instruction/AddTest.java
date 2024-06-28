package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.IO;
import model.ProgramCounter;
import model.Registry;
import org.junit.jupiter.api.Test;

class AddTest {
  @Test
  void testAddOperation() {
    Registry mockRegistry = mock(Registry.class);
    ProgramCounter mockPC = mock(ProgramCounter.class);
    IO mockIO = mock(IO.class);

    // Assuming that values have been loaded into registers OP1 and OP2
    when(mockRegistry.getRegister(Registry.REG_OP1)).thenReturn(5); // First operand
    when(mockRegistry.getRegister(Registry.REG_OP2)).thenReturn(10); // Second operand

    Add addInstruction = new Add(0);
    addInstruction.execute(null, mockRegistry, mockPC, mockIO);

    verify(mockRegistry).setRegister(Registry.REG_RES, 15);
  }

  @Test
  void testEvaluate() {
    Add addInstruction = new Add(0);
    assertEquals(InstructionFactory.INST_NAME_ADD, addInstruction.prettyPrint(null, null, 0));
  }
}
