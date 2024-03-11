package instruction;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import args.Operand;
import model.ObservableStorage;
import model.ProgramCounter;
import org.junit.jupiter.api.Test;

public class MovTest {

  @Test
  public void testMovOperation() {
    ObservableStorage mockMemory = mock(ObservableStorage.class);
    ProgramCounter mockPC = mock(ProgramCounter.class);

    // Setup for reading source and destination addresses
    when(mockPC.next())
        .thenReturn((int) 1, (int) 2); // Sequential addresses for source and destination
    int sourceValue = 42;
    when(mockMemory.getValueAt(Operand.of((int) 1)))
        .thenReturn(sourceValue); // Source value to move

    Mov movInstruction = new Mov(0);
    movInstruction.execute(mockMemory, mockPC);

    // Verify the source value is copied to the destination address
    verify(mockMemory)
        .setValueAt(
            eq(Operand.of((int) 2)), argThat(argument -> argument.getOperand() == sourceValue));
  }
}
