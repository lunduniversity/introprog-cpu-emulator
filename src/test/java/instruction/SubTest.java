package instruction;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import model.Registry;
import org.junit.jupiter.api.Test;

public class SubTest {

  @Test
  public void testSubOperation() {
    Registry mockRegistry = mock(Registry.class);

    when(mockRegistry.getRegister("OP1")).thenReturn(10); // First operand
    when(mockRegistry.getRegister("OP2")).thenReturn(5); // Second operand

    Sub subInstruction = new Sub(0);
    subInstruction.execute(null, mockRegistry, null);

    verify(mockRegistry).setRegister(eq("RES"), eq(5));
  }

  @Test
  public void testSubOperationWithNegativeResult() {
    // The instruction should not do any special handling for negative results. The byte storage
    // will handle it when the value is stored.
    Registry mockRegistry = mock(Registry.class);

    when(mockRegistry.getRegister("OP1")).thenReturn(10); // First operand
    when(mockRegistry.getRegister("OP2")).thenReturn(25); // Second operand

    Sub subInstruction = new Sub(0);
    subInstruction.execute(null, mockRegistry, null);

    verify(mockRegistry).setRegister(eq("RES"), eq(-15));
  }
}
