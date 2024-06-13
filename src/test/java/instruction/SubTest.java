package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.IO;
import model.ProgramCounter;
import model.Registry;
import org.junit.jupiter.api.Test;

class SubTest {

  @Test
  void testSubOperation() {
    Registry mockRegistry = mock(Registry.class);
    ProgramCounter mockPC = mock(ProgramCounter.class);
    IO mockIO = mock(IO.class);

    when(mockRegistry.getRegister(Registry.REG_OP1)).thenReturn(10); // First operand
    when(mockRegistry.getRegister(Registry.REG_OP2)).thenReturn(5); // Second operand

    Sub subInstruction = new Sub(0);
    subInstruction.execute(null, mockRegistry, mockPC, mockIO);

    verify(mockRegistry).setRegister(eq(Registry.REG_RES), eq(5));
  }

  @Test
  void testSubOperationWithNegativeResult() {
    // The instruction should not do any special handling for negative results. The byte storage
    // will handle it when the value is stored.
    Registry mockRegistry = mock(Registry.class);
    ProgramCounter mockPC = mock(ProgramCounter.class);
    IO mockIO = mock(IO.class);

    when(mockRegistry.getRegister(Registry.REG_OP1)).thenReturn(10); // First operand
    when(mockRegistry.getRegister(Registry.REG_OP2)).thenReturn(25); // Second operand

    Sub subInstruction = new Sub(0);
    subInstruction.execute(null, mockRegistry, mockPC, mockIO);

    verify(mockRegistry).setRegister(eq(Registry.REG_RES), eq(-15));
  }

  @Test
  void testEvaluate() {
    Sub subInstruction = new Sub(0);
    assertEquals(InstructionFactory.INST_NAME_SUB, subInstruction.evaluate(null, null, 0));
  }
}
