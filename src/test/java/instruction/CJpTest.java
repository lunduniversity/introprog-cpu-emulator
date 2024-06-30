package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import model.Memory;
import model.ProgramCounter;
import model.Registry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CJpTest {

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
  void testJumpWhenEqual() {
    int destinationAddress = 20; // Example destination address, not used in this test
    int operatorValue =
        Registry.nameToIdx(Registry.REG_RES) << 4 | Registry.nameToIdx(Registry.REG_R2);

    // Setup conditions where OP1 does not equal OP2
    when(mockMemory.size()).thenReturn(10);
    when(mockMemory.getValueAt(1)).thenReturn(operatorValue);
    when(mockMemory.getValueAt(2)).thenReturn(destinationAddress);
    when(mockRegistry.getValueAt(Registry.nameToIdx(Registry.REG_RES))).thenReturn(5);
    when(mockRegistry.getValueAt(Registry.nameToIdx(Registry.REG_R2))).thenReturn(5);
    when(mockPC.next()).thenReturn(0, 1, 2);

    CJp jeInstruction = new CJp(0);
    jeInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify jumpTo is called with the correct destination address
    verify(mockPC).jumpTo(destinationAddress);
  }

  @Test
  void testNoJumpWhenNotEqual() {
    int destinationAddress = 20; // Example destination address, not used in this test
    int operatorValue =
        Registry.nameToIdx(Registry.REG_RES) << 4 | Registry.nameToIdx(Registry.REG_R2);

    // Setup conditions where OP1 does not equal OP2
    when(mockMemory.size()).thenReturn(10);
    when(mockMemory.getValueAt(1)).thenReturn(operatorValue);
    when(mockMemory.getValueAt(2)).thenReturn(destinationAddress);
    when(mockRegistry.getValueAt(Registry.nameToIdx(Registry.REG_RES))).thenReturn(5);
    when(mockRegistry.getValueAt(Registry.nameToIdx(Registry.REG_R2))).thenReturn(10);
    when(mockPC.next()).thenReturn(0, 1, 2);

    CJp jeInstruction = new CJp(0);
    jeInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify jumpTo is never called since the conditions for jumping are not met
    verify(mockPC, never()).jumpTo(anyInt());
  }

  @Test
  void testPrettyPrint() {
    CJp jeq = new CJp(0); // Operand is not used in evaluate
    when(mockMemory.size()).thenReturn(10);
    when(mockMemory.getValueAt(1))
        .thenReturn(
            Registry.nameToIdx(Registry.REG_RES) << 4 | Registry.nameToIdx(Registry.REG_R2));
    when(mockMemory.getValueAt(2)).thenReturn(20);
    String expected =
        String.format(
            "%s (RES%sR3 %s 20)",
            InstructionFactory.INST_NAME_CJP, Instruction.EQUAL_CHAR, Instruction.RIGHT_ARROW_CHAR);
    assertEquals(expected, jeq.prettyPrint(mockMemory, mockRegistry, 0));
  }
}
