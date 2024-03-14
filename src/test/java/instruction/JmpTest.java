package instruction;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import model.ProgramCounter;
import model.Registry;
import org.junit.jupiter.api.Test;

public class JmpTest {
  @Test
  public void testJump() {
    Registry mockRegistry = mock(Registry.class);
    ProgramCounter mockPC = mock(ProgramCounter.class);

    // Assuming the operand specifies the register containing the destination address
    int operand = 0; // This could be any register index used in your tests
    int destinationAddress = 100; // Example destination address

    // Setup the expected behavior to fetch the destination address
    when(mockRegistry.getRegister(operand)).thenReturn(destinationAddress);

    Jmp jmpInstruction = new Jmp(operand);
    jmpInstruction.execute(null, mockRegistry, mockPC, null);

    // Verify that jumpTo is called with the correct destination address
    verify(mockPC).jumpTo(destinationAddress);
  }
}
