package instruction;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import args.Address;
import model.ObservableStorage;
import model.ProgramCounter;

public class AddTest {

    @Test
    public void testAddOperation() {
        ObservableStorage mockMemory = mock(ObservableStorage.class);
        ProgramCounter mockPC = mock(ProgramCounter.class);

        // Assuming pc starts at 0, and addresses for operands are 1 and 2, and for the
        // result is 3
        when(mockPC.next()).thenReturn((int) 1, (int) 2, (int) 3);
        when(mockMemory.getValueAt(Address.of((int) 1))).thenReturn((int) 5); // First operand
        when(mockMemory.getValueAt(Address.of((int) 2))).thenReturn((int) 10); // Second operand

        Add addInstruction = new Add();
        addInstruction.execute(mockMemory, mockPC);

        verify(mockMemory).setValueAt(eq(Address.of((int) 3)),
                argThat(argument -> argument.getValue(null) == (int) 15));
    }
}
