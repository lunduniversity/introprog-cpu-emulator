package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import model.ProgramCounter;
import model.Registry;
import org.junit.jupiter.api.Test;

public class JneTest {
  @Test
  public void testJumpWhenNotEqual() {
    Registry mockRegistry = mock(Registry.class);
    ProgramCounter mockPC = mock(ProgramCounter.class);

    // Assuming the operand specifies the register containing the destination address
    int operand = 0; // Use the appropriate operand for your test
    int destinationAddress = 200; // Example destination address

    // Setup conditions where OP1 does not equal OP2
    when(mockRegistry.getRegister("OP1")).thenReturn(5);
    when(mockRegistry.getRegister("OP2")).thenReturn(10);
    when(mockRegistry.getValueAt(operand)).thenReturn(destinationAddress);

    Jne jneInstruction = new Jne(operand);
    jneInstruction.execute(null, mockRegistry, mockPC, null);

    // Verify jumpTo is called with the correct destination address
    verify(mockPC).jumpTo(destinationAddress);
  }

  @Test
  public void testNoJumpWhenEqual() {
    Registry mockRegistry = mock(Registry.class);
    ProgramCounter mockPC = mock(ProgramCounter.class);

    int operand = 0; // The operand indicating the destination address register
    int destinationAddress = 200; // Example destination address, not used in this test

    // Setup conditions where OP1 equals OP2
    when(mockRegistry.getRegister("OP1")).thenReturn(5);
    when(mockRegistry.getRegister("OP2")).thenReturn(5);
    // The getRegister call for the operand might still happen, but should not result in a jump
    when(mockRegistry.getValueAt(operand)).thenReturn(destinationAddress);

    Jne jneInstruction = new Jne(operand);
    jneInstruction.execute(null, mockRegistry, mockPC, null);

    // Verify jumpTo is never called since the conditions for jumping are not met
    verify(mockPC, never()).jumpTo(anyInt());
  }

  @Test
  public void testToString() {
    Jne jumpOP1 = new Jne(Registry.nameToIdx("OP1"));
    Jne jumpRES = new Jne(Registry.nameToIdx("RES"));
    Jne jumpR3 = new Jne(Registry.nameToIdx("R3"));
    assertEquals(InstructionFactory.INST_NAME_JNE + " (dst: OP1)", jumpOP1.toString());
    assertEquals(InstructionFactory.INST_NAME_JNE + " (dst: RES)", jumpRES.toString());
    assertEquals(InstructionFactory.INST_NAME_JNE + " (dst: R3)", jumpR3.toString());
  }
}
