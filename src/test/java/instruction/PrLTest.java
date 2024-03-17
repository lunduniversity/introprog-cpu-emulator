package instruction;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.IO;
import model.Memory;
import model.ProgramCounter;
import model.Registry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.stubbing.OngoingStubbing;

public class PrLTest {

  private Memory mockMemory;
  private Registry mockRegistry;
  private ProgramCounter mockPC;
  private IO mockIO;

  @BeforeEach
  public void setUp() {
    mockMemory = mock(Memory.class);
    mockRegistry = mock(Registry.class);
    mockPC = mock(ProgramCounter.class);
    mockIO = mock(IO.class);
  }

  @Test
  public void testPrintLoopOperation() {

    // Create an InOrder verifier for mockIO
    InOrder inOrder = inOrder(mockIO);

    String message = "Hello, WorLd!";
    int offset = 30; // Memory address to start printing from
    for (int i = 0; i < message.length(); i++) {
      when(mockMemory.getValueAt(offset + i)).thenReturn((int) message.charAt(i));
    }

    // Assuming that start and end address have been loaded into registers OP1 and OP2
    OngoingStubbing<Integer> whenReg = when(mockRegistry.getRegister("OP1"));
    for (int i = 0; i < message.length(); i++) {
      whenReg = whenReg.thenReturn(offset + i);
    }
    when(mockRegistry.getRegister("OP2")).thenReturn(offset + message.length() - 1);

    PrL printLoopInstr = new PrL(0);

    // Execute the instruction. Note: This prints one character at a time, so we need to call it
    // until pc.next() is called.
    for (int i = 0; i < message.length(); i++) {
      printLoopInstr._execute(mockMemory, mockRegistry, mockPC, mockIO);
    }

    for (int i = 0; i < message.length(); i++) {
      int count = 1;
      while (i + count < message.length() - 1 && message.charAt(i) == message.charAt(i + count)) {
        count++;
        i++;
      }
      inOrder.verify(mockIO, times(count)).print((int) message.charAt(i));
    }

    // Verify that next() is called on the program counter after the last character is printed
    verify(mockPC, times(1)).next();
  }
}
