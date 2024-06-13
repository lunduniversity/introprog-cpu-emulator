package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import model.Memory;
import model.ProgramCounter;
import model.Registry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JmpTest {

  private Memory mockMemory;
  private Registry mockRegistry;
  private ProgramCounter mockPC;

  @BeforeEach
  void setup() {
    mockMemory = mock(Memory.class);
    mockRegistry = mock(Registry.class);
    mockPC = mock(ProgramCounter.class);
  }

  @Test
  void testJump() {
    int destinationAddress = 20; // Example destination address

    when(mockMemory.size()).thenReturn(10);
    when(mockMemory.getValueAt(1)).thenReturn(destinationAddress);
    when(mockPC.next()).thenReturn(0, 1, 2);

    Jmp jmpInstruction = new Jmp(0);
    jmpInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify that jumpTo is called with the correct destination address
    verify(mockPC).jumpTo(destinationAddress);
  }

  @Test
  void testEvaluate() {
    Jmp jmp = new Jmp(0);
    int destination = 20;
    when(mockMemory.size()).thenReturn(10);
    when(mockMemory.getValueAt(1)).thenReturn(destination);
    String expected = String.format("%s (dst: %d)", InstructionFactory.INST_NAME_JMP, destination);
    assertEquals(expected, jmp.evaluate(mockMemory, mockRegistry, 0));
  }
}
