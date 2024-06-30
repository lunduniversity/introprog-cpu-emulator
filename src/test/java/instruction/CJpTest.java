package instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
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
    when(mockMemory.size()).thenReturn(50);
  }

  @Test
  void testJumpWhenEqual() {
    int destinationAddress = 20;
    int op1 = Registry.nameToIdx(Registry.REG_R1);
    int op2 = Registry.nameToIdx(Registry.REG_R2);
    int addressesValue = (op1 << 4) | op2;

    // Setup operators and operands such that comparison is always true
    int[] operands =
        new int[] {
          CJp.EQUAL,
          CJp.NOT_EQUAL,
          CJp.LESS_THAN,
          CJp.LESS_THAN_OR_EQUAL,
          CJp.GREATER_THAN,
          CJp.GREATER_THAN_OR_EQUAL
        };
    Integer[] lhops = new Integer[] {5, 5, 5, 5, 5, 5};
    Integer[] rhops = new Integer[] {5, 7, 8, 5, 3, 0};

    // Destination address is always read from the RES register
    when(mockRegistry.getRegister(Registry.REG_RES)).thenReturn(destinationAddress);

    // Setup conditions where OP1 does not equal OP2
    when(mockMemory.getValueAt(1)).thenReturn(addressesValue);
    when(mockRegistry.getValueAt(op1))
        .thenReturn(lhops[0], Arrays.copyOfRange(lhops, 1, lhops.length));
    when(mockRegistry.getValueAt(op2))
        .thenReturn(rhops[0], Arrays.copyOfRange(rhops, 1, rhops.length));
    when(mockPC.next()).thenReturn(1);

    for (int operand : operands) {
      CJp jeInstruction = new CJp(operand);
      jeInstruction.execute(mockMemory, mockRegistry, mockPC, null);
    }
    // Verify jumpTo is called with the correct destination address
    verify(mockPC, times(operands.length)).jumpTo(destinationAddress);
  }

  @Test
  void testNoJumpWhenNotEqual() {
    int destinationAddress = 20;
    int op1 = Registry.nameToIdx(Registry.REG_R1);
    int op2 = Registry.nameToIdx(Registry.REG_R2);
    int addressesValue = (op1 << 4) | op2;

    // Setup operators and operands such that comparison is never true
    int[] operands =
        new int[] {
          CJp.EQUAL,
          CJp.NOT_EQUAL,
          CJp.LESS_THAN,
          CJp.LESS_THAN_OR_EQUAL,
          CJp.GREATER_THAN,
          CJp.GREATER_THAN_OR_EQUAL
        };
    Integer[] lhops = new Integer[] {5, 5, 5, 5, 5, 5};
    Integer[] rhops = new Integer[] {3, 5, 5, 1, 5, 6};

    // Destination address is always read from the RES register
    when(mockRegistry.getRegister(Registry.REG_RES)).thenReturn(destinationAddress);

    // Setup conditions where OP1 does not equal OP2
    when(mockMemory.getValueAt(1)).thenReturn(addressesValue);
    when(mockRegistry.getValueAt(op1))
        .thenReturn(lhops[0], Arrays.copyOfRange(lhops, 1, lhops.length));
    when(mockRegistry.getValueAt(op2))
        .thenReturn(rhops[0], Arrays.copyOfRange(rhops, 1, rhops.length));
    when(mockPC.next()).thenReturn(1);

    for (int operand : operands) {
      CJp jeInstruction = new CJp(operand);
      jeInstruction.execute(mockMemory, mockRegistry, mockPC, null);
    }

    // Verify jumpTo is never called since the conditions for jumping are not met
    verify(mockPC, never()).jumpTo(anyInt());
  }

  @Test
  void testPrettyPrint() {
    int[] operands =
        new int[] {
          CJp.EQUAL,
          CJp.NOT_EQUAL,
          CJp.LESS_THAN,
          CJp.LESS_THAN_OR_EQUAL,
          CJp.GREATER_THAN,
          CJp.GREATER_THAN_OR_EQUAL
        };

    String[] operandsStr =
        new String[] {
          Instruction.EQUAL_CHAR,
          Instruction.NOT_EQUAL_CHAR,
          Instruction.LESS_THAN_CHAR,
          Instruction.LESS_THAN_OR_EQUAL_CHAR,
          Instruction.GREATER_THAN_CHAR,
          Instruction.GREATER_THAN_OR_EQUAL_CHAR
        };

    when(mockMemory.size()).thenReturn(10);
    when(mockMemory.getValueAt(1))
        .thenReturn(Registry.nameToIdx(Registry.REG_R1) << 4 | Registry.nameToIdx(Registry.REG_R2));

    for (int i = 0; i < operands.length; i++) {
      int op = operands[i];
      String opStr = operandsStr[i];
      CJp jeInstruction = new CJp(op);
      String expected =
          String.format(
              "%s (R1%sR2 %s *RES)",
              InstructionFactory.INST_NAME_CJP, opStr, Instruction.RIGHT_ARROW_CHAR);
      assertEquals(expected, jeInstruction.prettyPrint(mockMemory, mockRegistry, 0));
    }
  }
}
