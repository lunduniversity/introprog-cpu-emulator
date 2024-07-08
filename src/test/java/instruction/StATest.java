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

class StATest {

  private Memory mockMemory;
  private Registry mockRegistry;
  private ProgramCounter mockPC;

  @BeforeEach
  void setup() {
    mockMemory = mock(Memory.class);
    mockRegistry = mock(Registry.class);
    mockPC = mock(ProgramCounter.class);

    when(mockMemory.size()).thenReturn(10);
  }

  @Test
  void testStoreOperation() {

    // Setup source and destination registers
    int op1 = Registry.nameToIdx(Registry.REG_R1);
    int op2 = Registry.nameToIdx(Registry.REG_RES);
    int operand = (op1 << 4) | op2;
    int registerValue = 35; // The value stored in the source register
    int memoryAddress = 8; // The destination address in memory where the value should be stored
    int nextMemoryLocation = 1; // The next memory location according to the program counter

    // Simulate the program counter pointing to the next memory address that contains the
    // destination address
    when(mockPC.next()).thenReturn(nextMemoryLocation);

    // Simulate fetching the operand from memory
    when(mockMemory.getValueAt(nextMemoryLocation)).thenReturn(operand);

    // Simulate fetching the value from the register specified by the operand
    when(mockRegistry.getValueAt(op1)).thenReturn(registerValue);
    when(mockRegistry.getValueAt(op2)).thenReturn(memoryAddress);

    StA stInstruction = new StA(0);
    stInstruction.execute(mockMemory, mockRegistry, mockPC, null);

    // Verify memory's setValueAt method is called with the correct destination address and value
    verify(mockMemory).getValueAt(nextMemoryLocation);
    verify(mockMemory).setValueAt(memoryAddress, registerValue);
  }

  @Test
  void testPrettyPrint() {
    StA store = new StA(0);
    int leftOp = Registry.nameToIdx(Registry.REG_R2);
    int rightOp = Registry.nameToIdx(Registry.REG_OP1);
    int operand = (leftOp << 4) | rightOp;
    when(mockMemory.getValueAt(1)).thenReturn(operand);
    assertEquals(
        InstructionFactory.INST_NAME_STA + " (R2 " + Instruction.RIGHT_ARROW_CHAR + " *OP1)",
        store.prettyPrint(mockMemory, mockRegistry, 0));
  }
}
