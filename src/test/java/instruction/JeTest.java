package instruction;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import model.ProgramCounter;
import model.Registry;
import org.junit.jupiter.api.Test;

public class JeTest {
  @Test
  public void testJumpWhenEqual() {
    Registry mockRegistry = mock(Registry.class);
    ProgramCounter mockPC = mock(ProgramCounter.class);

    // Assuming the operand specifies the register containing the destination address
    int operand = 0; // This could be any register index used in your tests
    int destinationAddress = 100; // Example destination address

    // Setup conditions where OP1 equals OP2
    when(mockRegistry.getRegister("OP1")).thenReturn(5);
    when(mockRegistry.getRegister("OP2")).thenReturn(5);
    when(mockRegistry.getRegister(operand)).thenReturn(destinationAddress);

    Je jeInstruction = new Je(operand);
    jeInstruction._execute(null, mockRegistry, mockPC, null);

    // Verify jumpTo is called with the correct destination address
    verify(mockPC).jumpTo(destinationAddress);
  }

  @Test
  public void testNoJumpWhenNotEqual() {
    Registry mockRegistry = mock(Registry.class);
    ProgramCounter mockPC = mock(ProgramCounter.class);

    int operand = 0; // The operand indicating the destination address register
    int destinationAddress = 100; // Example destination address, not used in this test

    // Setup conditions where OP1 does not equal OP2
    when(mockRegistry.getRegister("OP1")).thenReturn(5);
    when(mockRegistry.getRegister("OP2")).thenReturn(10);
    // Even though this call happens, it should not lead to a jump since OP1 != OP2
    when(mockRegistry.getRegister(operand)).thenReturn(destinationAddress);

    Je jeInstruction = new Je(operand);
    jeInstruction._execute(null, mockRegistry, mockPC, null);

    // Verify jumpTo is never called since the conditions for jumping are not met
    verify(mockPC, never()).jumpTo(anyInt());
  }
}
