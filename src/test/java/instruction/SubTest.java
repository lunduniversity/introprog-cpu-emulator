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

public class SubTest {

  @Test
  public void testSubOperation() {
    ObservableStorage mockMemory = mock(ObservableStorage.class);
    ProgramCounter mockPC = mock(ProgramCounter.class);

    // Assuming pc starts at 0, and addresses for operands are 1 and 2, and for the
    // result is 3
    when(mockPC.next()).thenReturn((int) 1, (int) 2, (int) 3);
    when(mockMemory.getValueAt(Operand.of((int) 1))).thenReturn((int) 10); // First operand
    when(mockMemory.getValueAt(Operand.of((int) 2))).thenReturn((int) 5); // Second operand

    Sub subInstruction = new Sub(0);
    subInstruction.execute(mockMemory, mockPC);

    verify(mockMemory)
        .setValueAt(eq(Operand.of((int) 3)), argThat(argument -> argument.getOperand() == (int) 5));
  }
}
